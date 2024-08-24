package me.thepond.soltribes.block.entity;

import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.registry.ModBlockEntities;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TribeFlagBlockEntity extends BlockEntity {

    private UUID tribeSettlementId;
    private UUID tribeId;

    private Tribe tribe;
    private TribeSettlement tribeSettlement;

    private NbtCompound bannerNbt;

    public TribeFlagBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIBE_FLAG_BLOCK_ENTITY, pos, state);
        this.bannerNbt = new NbtCompound();
    }

    public UUID getTribeSettlementId() {
        return tribeSettlementId;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public UUID getTribeId() {
        return tribeId;
    }

    public NbtCompound getBannerNbt() {
        return bannerNbt;
    }

    public void setupSettlementFlag(UUID tribeId, UUID tribeSettlementId) {
        this.tribeId = tribeId;
        this.tribeSettlementId = tribeSettlementId;
        if (world != null && !world.isClient) {
            TribesData tribeData = TribesDataManager.getTribesData((ServerWorld) world);
            this.tribe = tribeData.getTribe(tribeId);
            this.tribeSettlement = tribe.getTribeSettlement(tribeSettlementId);
            this.bannerNbt = tribe.getTribeBanner();
            markDirty();
            sendUpdatePacket();
        }
    }

    public void onRemove(World world) {
        if (!world.isClient) {
            if (tribe != null) {
                List<TribeMember> allTribeMembers = new ArrayList<>(tribeSettlement.getTribeMembersInRange());

                List<TribeMember> tribeMembersInRange = world.getEntitiesByClass(
                        PlayerEntity.class,
                        Box.from(new BlockBox(pos)).expand(200.0D),
                        player -> tribe.getTribeMembers().stream().anyMatch(tribeMember -> tribeMember.getTribeMemberUUID().equals(player.getUuid()))
                ).stream().map(player -> tribe.getTribeMember(player.getUuid())).toList();

                allTribeMembers.addAll(tribeMembersInRange);

                if (tribe.getTribeActiveEffect() != null) {
                    tribe.getTribeActiveEffect().untick(allTribeMembers, tribeSettlement, (ServerWorld) world);
                }

                if (tribe.getLastActiveEffect() != null) {
                    tribe.getLastActiveEffect().untick(allTribeMembers, tribeSettlement, (ServerWorld) world);
                }
            }
        }
    }

    public void updateTribeBanner(NbtCompound bannerNbt) {
        this.bannerNbt = bannerNbt;
        markDirty();
        sendUpdatePacket();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            if (tribe != null) {

                if (tribe.isDisbanded()) {
                    world.breakBlock(pos, true);
                    return;
                }

                if (tribeSettlement != null) {
                    List<TribeMember> tribeMembersInRange = world.getEntitiesByClass(
                            PlayerEntity.class,
                            Box.from(new BlockBox(pos)).expand(200.0D),
                            player -> tribe.getTribeMembers().stream().anyMatch(tribeMember -> tribeMember.getTribeMemberUUID().equals(player.getUuid()))
                    ).stream().map(player -> tribe.getTribeMember(player.getUuid())).toList();

                    List<TribeMember> tribeMembersToRemove = new ArrayList<>();
                    for (TribeMember tribeMember : tribeSettlement.getTribeMembersInRange()) {
                        if (!tribeMembersInRange.contains(tribeMember)) {
                            tribeMembersToRemove.add(tribeMember);
                        }
                    }

                    tribeSettlement.setTribeMembersInRange(tribeMembersInRange);

                    if (tribe.getTribeActiveEffect() != null) {
                        tribe.getTribeActiveEffect().tick(tribeSettlement, (ServerWorld) world);
                        tribe.getTribeActiveEffect().untick(tribeMembersToRemove, tribeSettlement, (ServerWorld) world);
                    }

                    if (tribe.getLastActiveEffect() != null) {
                        List<TribeMember> tribeMembers = new ArrayList<>(tribeSettlement.getTribeMembersInRange());
                        tribeMembers.addAll(tribeMembersToRemove);
                        tribe.getLastActiveEffect().untick(tribeMembers, tribeSettlement, (ServerWorld) world);
                    }
                }

                if (bannerNbt.isEmpty() && !tribe.getTribeBanner().isEmpty()) {
                    updateTribeBanner(tribe.getTribeBanner());
                }
            }
        }
    }

    private void sendUpdatePacket() {
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.pos);
        buf.writeRegistryValue(Registries.BLOCK_ENTITY_TYPE, ModBlockEntities.TRIBE_FLAG_BLOCK_ENTITY);
        buf.writeNbt(nbt);
        return new BlockEntityUpdateS2CPacket(buf);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (tribeSettlementId != null) {
            nbt.putUuid("TribeSettlementId", tribeSettlementId);
        }
        if (tribeId != null) {
            nbt.putUuid("TribeId", tribeId);
        }
        nbt.put("TribeBanner", bannerNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        tribeSettlementId = nbt.contains("TribeSettlementId") ? nbt.getUuid("TribeSettlementId") : null;
        tribeId = nbt.contains("TribeId") ? nbt.getUuid("TribeId") : null;
        bannerNbt = nbt.contains("TribeBanner") ? nbt.getCompound("TribeBanner") : new NbtCompound();
        if (world != null && !world.isClient) {
            TribesData tribeData = TribesDataManager.getTribesData((ServerWorld) world);
            tribe = tribeData.getTribe(tribeId);
            tribeSettlement = tribe.getTribeSettlement(tribeSettlementId);
        }
    }
}
