package me.thepond.soltribes.tribe;

import me.thepond.soltribes.SOLTribesServerConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TribeSettlement {

    private UUID tribeSettlementId;
    private String tribeSettlementName;
    private String tribeSettlementDescription;
    private Date tribeSettlementCreationDate;
    private BlockPos tribeSettlementPosition;

    private List<TribeMember> tribeMembersInRange = new ArrayList<>();

    public TribeSettlement() {
    }

    public TribeSettlement(String tribeSettlementName, String tribeSettlementDescription, Date tribeSettlementCreationDate, BlockPos tribeSettlementPosition) {
        this.tribeSettlementId = UUID.randomUUID();
        this.tribeSettlementName = tribeSettlementName;
        this.tribeSettlementDescription = tribeSettlementDescription;
        this.tribeSettlementCreationDate = tribeSettlementCreationDate;
        this.tribeSettlementPosition = tribeSettlementPosition;
    }

    public UUID getTribeSettlementId() {
        return tribeSettlementId;
    }

    public String getTribeSettlementName() {
        return tribeSettlementName;
    }

    public String getTribeSettlementDescription() {
        return tribeSettlementDescription;
    }

    public Date getTribeSettlementCreationDate() {
        return tribeSettlementCreationDate;
    }

    public BlockPos getTribeSettlementPosition() {
        return tribeSettlementPosition;
    }

    public List<TribeMember> getTribeMembersInRange() {
        return tribeMembersInRange;
    }

    public void addTribeMemberInRange(TribeMember tribeMember) {
        tribeMembersInRange.add(tribeMember);
    }

    public void removeTribeMemberInRange(TribeMember tribeMember) {
        tribeMembersInRange.remove(tribeMember);
    }

    public void setTribeMembersInRange(List<TribeMember> tribeMembersInRange) {
        this.tribeMembersInRange = tribeMembersInRange;
    }

    public boolean isTribeMemberInRange(ServerWorld world, TribeMember tribeMember) {
        ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeMember.getTribeMemberUUID());
        if (player == null) return false;
        double distanceFromFlag = player.getPos().distanceTo(new Vec3d(tribeSettlementPosition.getX(), tribeSettlementPosition.getY(), tribeSettlementPosition.getZ()));
        return distanceFromFlag <= SOLTribesServerConfig.getDouble(SOLTribesServerConfig.FLAG_EFFECT_DISTANCE, 200.0D);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("tribeSettlementId", tribeSettlementId);
        nbt.putString("tribeSettlementName", tribeSettlementName);
        nbt.putString("tribeSettlementDescription", tribeSettlementDescription);
        nbt.putLong("tribeSettlementCreationDate", tribeSettlementCreationDate.getTime());
        nbt.putLong("tribeSettlementPosition", tribeSettlementPosition.asLong());
        return nbt;
    }

    public static TribeSettlement fromNbt(NbtCompound nbt) {
        TribeSettlement tribeSettlement = new TribeSettlement();
        tribeSettlement.tribeSettlementId = nbt.getUuid("tribeSettlementId");
        tribeSettlement.tribeSettlementName = nbt.getString("tribeSettlementName");
        tribeSettlement.tribeSettlementDescription = nbt.getString("tribeSettlementDescription");
        tribeSettlement.tribeSettlementCreationDate = new Date(nbt.getLong("tribeSettlementCreationDate"));
        tribeSettlement.tribeSettlementPosition = BlockPos.fromLong(nbt.getLong("tribeSettlementPosition"));
        return tribeSettlement;
    }
}
