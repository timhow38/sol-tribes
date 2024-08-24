package me.thepond.soltribes.block.entity;

import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.registry.ModBlockEntities;
import me.thepond.soltribes.screenhandler.*;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.utils.PlayerInfo;
import me.thepond.soltribes.packets.VisitType;
import me.thepond.soltribes.registry.ModBlocks;
import me.thepond.soltribes.registry.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TribeTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private static final int BANNER_INPUT_SLOT = 0;
    private static final int FLAG_INPUT_SLOT = 1;

    private final Inventory infoPageInventory = new ImplementedInventory() {
        private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
        @Override
        public DefaultedList<ItemStack> getItems() {
            return items;
        }

        @Override
        public void markDirty() {
            TribeTableBlockEntity.this.markDirty();
        }
    };
    private final Inventory flagsPageInventory = new ImplementedInventory() {
        private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
        @Override
        public DefaultedList<ItemStack> getItems() {
            return items;
        }

        @Override
        public void markDirty() {
            TribeTableBlockEntity.this.markDirty();
        }
    };

    private final List<PlayerInfo> tribeFounders = new ArrayList<>();
    private final List<UUID> playersInteracting = new ArrayList<>();

    private Tribe tribe;
    private boolean activated;

    private ItemStack lastBannerInput;

    public TribeTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIBE_TABLE_BLOCK_ENTITY, pos, state);
        this.activated = false;
    }

    public void createNewTribe() {
        List<TribeMember> tribeFounders = new ArrayList<>();
        this.tribeFounders.forEach(playerInfo -> {
            tribeFounders.add(new TribeMember(playerInfo.getPlayerUUID(), playerInfo.getPlayerName(), new Date()));
        });
        this.tribe = new Tribe("New Tribe", "New Tribe Description", "", pos, new NbtCompound(), tribeFounders, new ArrayList<>(), new ArrayList<>());
        TribesDataManager.getTribesData((ServerWorld)world).addOrUpdateTribe(tribe);
        markDirty();
        sendUpdatePacket();
    }

    public void createSettlementFlag(ServerPlayerEntity player, String settlementName, String settlementDescription) {
        if (settlementName.isBlank() || settlementDescription.isBlank()) {
            return;
        }
        ItemStack stack = flagsPageInventory.getStack(0);
        if (stack.isEmpty() || !stack.isOf(ModBlocks.TRIBE_FLAG.asItem())) {
            return;
        }

        NbtCompound compound = stack.getOrCreateNbt();

        compound.putUuid("TribeId", tribe.getTribeId());
        compound.putString("SettlementName", settlementName);
        compound.putString("SettlementDescription", settlementDescription);

        EnchantmentHelper.set(Map.of(Enchantments.UNBREAKING, 1), stack);
    }

    public void updateTribe(Tribe tribe) {
        if (tribe.isDisbanded()) {
            disbandTable();
        } else {
            this.tribe = tribe;
            this.activated = true;
        }
        markDirty();
        sendUpdatePacket();
    }

    public boolean addTribeMember(UUID tribeMember) {
        ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeMember);
        PlayerInfo playerInfo = new PlayerInfo(player.getName().getString(), tribeMember);
        TribesData tribesData = TribesDataManager.getTribesData((ServerWorld) world);
        Tribe tribe = tribesData.getTribe(this.tribe.getTribeId());
        if (tribe.getTribeMembers().stream().noneMatch(member -> member.getTribeMemberUUID().equals(tribeMember))) {
            tribe.addTribeMember(new TribeMember(playerInfo.getPlayerUUID(), playerInfo.getPlayerName(), new Date()));
            tribesData.setDirty(true);
            updateTribe(tribe);
            markDirty();
            sendUpdatePacket();
            player.openHandledScreen(this);
            return true;
        }
        return false;
    }

    public boolean addTribeFounder(UUID tribeFounder) {
        if (tribeFounders.size() < 3 && tribeFounders.stream().noneMatch(playerInfo -> playerInfo.getPlayerUUID().equals(tribeFounder))) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeFounder);
            PlayerInfo playerInfo = new PlayerInfo(player.getName().getString(), tribeFounder);

            tribeFounders.add(playerInfo);

            markDirty();
            sendUpdatePacket();

            checkForActivation();
            return true;
        }
        return false;
    }

    private void sendUpdatePacket() {
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public boolean checkForActivation() {
        if (tribeFounders.size() == 3) {
            activated = true;
            createNewTribe();
            for (UUID uuid : new ArrayList<>(playersInteracting)) {
                PlayerEntity player = world.getPlayerByUuid(uuid);
                if (player != null) {
                    player.openHandledScreen(this);
                }
            }
            return true;
        }
        return false;
    }

    private void disbandTable() {
        this.tribe = null;
        this.activated = false;
        this.infoPageInventory.clear();
        this.flagsPageInventory.clear();
        this.tribeFounders.clear();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Tribe Table");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        // Write info page inventory
        NbtCompound infoPageInventoryNbt = new NbtCompound();
        Inventories.writeNbt(infoPageInventoryNbt, ((ImplementedInventory)infoPageInventory).getItems());
        nbt.put("infoPageInventory", infoPageInventoryNbt);
        // Write flags page inventory
        NbtCompound flagsPageInventoryNbt = new NbtCompound();
        Inventories.writeNbt(flagsPageInventoryNbt, ((ImplementedInventory)flagsPageInventory).getItems());
        nbt.put("flagsPageInventory", flagsPageInventoryNbt);
        // Write tribe founders
        NbtCompound tribeFoundersNbt = new NbtCompound();
        for (int i = 0; i < tribeFounders.size(); i++) {
            tribeFoundersNbt.put("founder" + i, tribeFounders.get(i).toNbt());
        }
        nbt.put("tribeFounders", tribeFoundersNbt);
        // Extra data
        nbt.putBoolean("activated", activated);
        // Tribe
        if (tribe != null) {
            nbt.put("tribe", tribe.toNbt());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // Read info page inventory
        NbtCompound infoPageInventoryNbt = nbt.getCompound("infoPageInventory");
        Inventories.readNbt(infoPageInventoryNbt, ((ImplementedInventory)infoPageInventory).getItems());
        // Read flags page inventory
        NbtCompound flagsPageInventoryNbt = nbt.getCompound("flagsPageInventory");
        Inventories.readNbt(flagsPageInventoryNbt, ((ImplementedInventory)flagsPageInventory).getItems());
        // Read tribe founders
        tribeFounders.clear();
        NbtCompound tribeFoundersNbt = nbt.getCompound("tribeFounders");
        for (int i = 0; i < tribeFoundersNbt.getSize(); i++) {
            tribeFounders.add(PlayerInfo.fromNbt(tribeFoundersNbt.getCompound("founder" + i)));
        }
        // Extra data
        activated = nbt.getBoolean("activated");
        // Tribe
        if (nbt.contains("tribe")) {
            tribe = Tribe.fromNbt(nbt.getCompound("tribe"));
            if (tribe.isDisbanded()) {
                disbandTable();
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.pos);
        buf.writeRegistryValue(Registries.BLOCK_ENTITY_TYPE, ModBlockEntities.TRIBE_TABLE_BLOCK_ENTITY);
        buf.writeNbt(nbt);
        return new BlockEntityUpdateS2CPacket(buf);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    @Nullable
    public ScreenHandler openScreenTab(int tabId, int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        playersInteracting.add(player.getUuid());
        if (activated) {
            if (tabId == 0) {
                return createMenu(syncId, playerInventory, player);
            } else if (tabId == 1) {
                return new TribeTableFlagScreenHandlerBlockEntity(syncId, playerInventory, this);
            } else if (tabId == 2) {
                return new TribeTableEffectsScreenHandlerBlockEntity(syncId, playerInventory, this);
            }
        }
        playersInteracting.remove(player.getUuid());
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        playersInteracting.add(player.getUuid());
        TribesData tribesData = TribesDataManager.getTribesData((ServerWorld) world);
        Tribe foundTribe = tribesData.getTribeFromPlayer(player.getUuid());
        if (foundTribe == null) {
            if (tribe != null && activated) {
                return new TribeTableJoinScreenHandlerBlockEntity(syncId, playerInventory, this);
            } else {
                return new TribeTableActivationScreenHandlerBlockEntity(syncId, playerInventory, this);
            }
        } else {
            if (tribe != null && tribe.getTribeId().equals(foundTribe.getTribeId())) {
                return new TribeTableInformationScreenHandlerBlockEntity(syncId, playerInventory, this);
            } else {
                playersInteracting.remove(player.getUuid());
                return null;
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            if (tribe != null) {
                ItemStack stack = infoPageInventory.getStack(0);
                if (!stack.isEmpty() && stack.isIn(ItemTags.BANNERS)) {
                    if (lastBannerInput == null || !ItemStack.areEqual(lastBannerInput, stack)) {
                        lastBannerInput = stack.copy();
                        NbtCompound bannerNbt = new NbtCompound();
                        BannerItem bannerItem = (BannerItem) stack.getItem();
                        bannerNbt.putInt("Color", bannerItem.getColor().getId());
                        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
                        if (nbtCompound != null && nbtCompound.contains("Patterns")) {
                            bannerNbt.put("Patterns", nbtCompound.getList("Patterns", 10).copy());
                        }
                        TribesData tribesData = TribesDataManager.getTribesData((ServerWorld) world);
                        Tribe foundTribe = tribesData.getTribe(tribe.getTribeId());
                        if (foundTribe != null) {
                            foundTribe.setTribeBanner(bannerNbt);
                            tribesData.setDirty(true);
                            updateTribe(foundTribe);
                            foundTribe.getTribeSettlements().forEach(tribeSettlement -> {
                                if (world.getBlockEntity(tribeSettlement.getTribeSettlementPosition()) instanceof TribeFlagBlockEntity tribeFlagBlockEntity) {
                                    tribeFlagBlockEntity.updateTribeBanner(bannerNbt);
                                }
                            });
                            world.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeEnumConstant(VisitType.UPDATE);
                                buf.writeUuid(tribe.getTribeId());
                                buf.writeNbt(bannerNbt);
                                ServerPlayNetworking.send(player, ModPackets.REGION_VISIT, buf);
                            });
                        }
                    }
                }
            }
        }
    }

    public List<PlayerInfo> getTribeFounders() {
        return tribeFounders;
    }

    public void closeScreen(ServerPlayerEntity player) {
        playersInteracting.remove(player.getUuid());
    }

    public Inventory getFlagsPageInventory() {
        return flagsPageInventory;
    }

    public Inventory getInfoPageInventory() {
        return infoPageInventory;
    }

    public Tribe getTribe() {
        if (tribe == null) {
            return new Tribe();
        }
        return tribe;
    }

    public boolean isActivated() {
        return activated;
    }

    public DefaultedList<ItemStack> getAllItems() {
        DefaultedList<ItemStack> allItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
        allItems.set(0, infoPageInventory.getStack(0));
        allItems.set(1, flagsPageInventory.getStack(0));
        return allItems;
    }
}
