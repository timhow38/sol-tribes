package me.thepond.soltribes.tribe;

import me.thepond.soltribes.SOLTribesServerConfig;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.tribe.effects.ITribeEffect;
import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.tribe.effects.TribeEffects;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tribe {

    private UUID tribeId;
    private String tribeName;
    private String tribeDescription;
    private String tribeDiscordUrl;

    private boolean disbanded;

    @Nullable
    private BlockPos tribeTablePos;

    private NbtCompound tribeBanner;

    @Nullable
    private ITribeEffect tribeActiveEffect;

    @Nullable
    private ITribeEffect lastActiveEffect;

    private final List<TribeMember> tribeFounders;
    private final List<TribeMember> tribeMembers;

    private final List<TribeSettlement> tribeSettlements;

    private boolean temp;

    public Tribe() {
        tribeId = UUID.randomUUID();
        tribeFounders = new ArrayList<>();
        tribeMembers = new ArrayList<>();
        tribeSettlements = new ArrayList<>();
        tribeBanner = new NbtCompound();
        this.temp = true;
    }

    public Tribe(String tribeName, String tribeDescription, String tribeDiscordUrl, @Nullable BlockPos tribeTablePos, NbtCompound tribeBanner, List<TribeMember> tribeFounders, List<TribeMember> tribeMembers, List<TribeSettlement> tribeSettlements) {
        this.tribeId = UUID.randomUUID();
        this.tribeName = tribeName;
        this.tribeDescription = tribeDescription;
        this.tribeDiscordUrl = tribeDiscordUrl;
        this.tribeTablePos = tribeTablePos;
        this.tribeBanner = tribeBanner;
        this.tribeFounders = tribeFounders;
        this.tribeMembers = tribeMembers;
        this.tribeSettlements = tribeSettlements;
        this.temp = false;
    }

    public UUID getTribeId() {
        return tribeId;
    }

    public String getTribeName() {
        return tribeName;
    }

    public String getTribeDescription() {
        return tribeDescription;
    }

    public String getTribeDiscordUrl() {
        return tribeDiscordUrl;
    }

    public BlockPos getTribeTablePos() {
        return tribeTablePos;
    }

    public NbtCompound getTribeBanner() {
        return tribeBanner;
    }

    public boolean isDisbanded() {
        return disbanded;
    }

    public @Nullable ITribeEffect getTribeActiveEffect() {
        return tribeActiveEffect;
    }

    public @Nullable ITribeEffect getLastActiveEffect() {
        return lastActiveEffect;
    }

    public List<TribeMember> getTribeFounders() {
        return tribeFounders;
    }

    public List<TribeMember> getTribeMembers() {
        List<TribeMember> tribeMemberList = new ArrayList<>();
        tribeMemberList.addAll(tribeFounders);
        tribeMemberList.addAll(tribeMembers);
        return tribeMemberList;
    }

    public TribeMember getTribeMember(UUID tribeMemberId) {
        for (TribeMember tribeMember : this.getTribeMembers()) {
            if (tribeMember.getTribeMemberUUID().equals(tribeMemberId)) {
                return tribeMember;
            }
        }
        return null;
    }

    public TribeSettlement getTribeSettlement(UUID tribeSettlementId) {
        for (TribeSettlement tribeSettlement : tribeSettlements) {
            if (tribeSettlement.getTribeSettlementId().equals(tribeSettlementId)) {
                return tribeSettlement;
            }
        }
        return null;
    }

    public List<TribeSettlement> getTribeSettlements() {
        return tribeSettlements;
    }

    public boolean isFounder(UUID tribeMemberId) {
        return tribeFounders.stream().anyMatch(member -> member.getTribeMemberUUID().equals(tribeMemberId));
    }

    public void removeTribeSettlement(UUID tribeSettlementId) {
        tribeSettlements.removeIf(settlement -> settlement.getTribeSettlementId().equals(tribeSettlementId));
    }

    public boolean isTemp() {
        return temp;
    }

    public int getSettlementCapacity() {
        int capacity = 1 + this.getTribeMembers().size() / SOLTribesServerConfig.getInt(SOLTribesServerConfig.FLAG_CAPACITY_SCALING, 3);
        int maxCapacity = SOLTribesServerConfig.getInt(SOLTribesServerConfig.FLAG_MAX_CAPACITY, 5);
        return Math.min(maxCapacity, Math.max(0, capacity - this.getTribeSettlements().size()));
    }

    public void addTribeMember(TribeMember tribeMember) {
        tribeMembers.add(tribeMember);
    }

    public void setDisbanded(boolean disbanded) {
        this.disbanded = disbanded;
    }

    public void removeTribeMember(UUID tribeMemberId) {
        tribeMembers.removeIf(member -> member.getTribeMemberUUID().equals(tribeMemberId));
        tribeFounders.removeIf(member -> member.getTribeMemberUUID().equals(tribeMemberId));
    }

    public void setTribeName(String tribeName) {
        this.tribeName = tribeName;
    }

    public void setTribeDescription(String tribeDescription) {
        this.tribeDescription = tribeDescription;
    }

    public void setTribeDiscordUrl(String tribeDiscordUrl) {
        this.tribeDiscordUrl = tribeDiscordUrl;
    }

    public void setTribeActiveEffect(@Nullable ITribeEffect tribeActiveEffect) {
        this.lastActiveEffect = this.tribeActiveEffect;
        this.tribeActiveEffect = tribeActiveEffect;
    }

    public void setTribeTablePos(@Nullable BlockPos tribeTablePos) {
        this.tribeTablePos = tribeTablePos;
    }

    public void setTribeBanner(NbtCompound tribeBanner) {
        this.tribeBanner = tribeBanner;
    }

    public void sendTableUpdate(World world) {
        if (tribeTablePos != null) {
            if (world.getBlockEntity(tribeTablePos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
                tribeTableBlockEntity.updateTribe(this);
            }
        }
    }

    public void syncTribeMembers(MinecraftServer server) {
        for (TribeMember member : this.getTribeMembers()) {
            ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(member.getTribeMemberUUID());
            if (serverPlayerEntity != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeNbt(this.toNbt());
                ServerPlayNetworking.send(serverPlayerEntity, ModPackets.SYNC_TRIBE, buf);
            }
        }
    }

    // Serialization methods

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("tribeId", tribeId);
        nbt.putString("tribeName", tribeName);
        nbt.putString("tribeDescription", tribeDescription);
        nbt.putString("tribeDiscordUrl", tribeDiscordUrl);
        nbt.putBoolean("tribeDisbanded", disbanded);
        if (tribeTablePos != null) {
            nbt.putLong("tribeTablePos", tribeTablePos.asLong());
        }
        nbt.put("tribeBanner", tribeBanner);
        nbt.putInt("tribeActiveEffect", tribeActiveEffect != null ? tribeActiveEffect.getTribeEffectId() : -1);

        NbtCompound founders = new NbtCompound();
        for (int i = 0; i < tribeFounders.size(); i++) {
            founders.put("founder" + i, tribeFounders.get(i).toNbt());
        }

        NbtCompound members = new NbtCompound();
        for (int i = 0; i < tribeMembers.size(); i++) {
            members.put("member" + i, tribeMembers.get(i).toNbt());
        }

        NbtCompound settlements = new NbtCompound();
        for (int i = 0; i < tribeSettlements.size(); i++) {
            settlements.put("settlement" + i, tribeSettlements.get(i).toNbt());
        }

        nbt.put("tribeFounders", founders);
        nbt.put("tribeMembers", members);
        nbt.put("tribeSettlements", settlements);

        return nbt;
    }

    public static Tribe fromNbt(NbtCompound nbt) {
        Tribe tribe = new Tribe();
        tribe.tribeId = nbt.getUuid("tribeId");
        tribe.tribeName = nbt.getString("tribeName");
        tribe.tribeDescription = nbt.getString("tribeDescription");
        tribe.tribeDiscordUrl = nbt.getString("tribeDiscordUrl");
        tribe.tribeTablePos = nbt.contains("tribeTablePos") ? BlockPos.fromLong(nbt.getLong("tribeTablePos")) : null;
        tribe.tribeBanner = nbt.getCompound("tribeBanner");
        tribe.tribeActiveEffect = TribeEffects.getTribeEffectById(nbt.getInt("tribeActiveEffect"));
        tribe.disbanded = nbt.getBoolean("tribeDisbanded");

        NbtCompound founders = nbt.getCompound("tribeFounders");
        for (String key : founders.getKeys()) {
            tribe.tribeFounders.add(TribeMember.fromNbt(founders.getCompound(key)));
        }

        NbtCompound members = nbt.getCompound("tribeMembers");
        for (String key : members.getKeys()) {
            tribe.tribeMembers.add(TribeMember.fromNbt(members.getCompound(key)));
        }

        NbtCompound settlements = nbt.getCompound("tribeSettlements");
        for (String key : settlements.getKeys()) {
            tribe.tribeSettlements.add(TribeSettlement.fromNbt(settlements.getCompound(key)));
        }

        tribe.temp = false;

        return tribe;
    }
}
