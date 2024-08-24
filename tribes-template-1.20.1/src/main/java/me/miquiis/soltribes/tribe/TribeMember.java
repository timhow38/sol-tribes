package me.thepond.soltribes.tribe;

import net.minecraft.nbt.NbtCompound;

import java.util.Date;
import java.util.UUID;

public class TribeMember {

    private UUID tribeMemberUUID;
    private String tribeMemberName;
    private Date tribeMemberJoinDate;

    public TribeMember() {
    }

    public TribeMember(UUID tribeMemberUUID, String tribeMemberName, Date tribeMemberJoinDate) {
        this.tribeMemberUUID = tribeMemberUUID;
        this.tribeMemberName = tribeMemberName;
        this.tribeMemberJoinDate = tribeMemberJoinDate;
    }

    public UUID getTribeMemberUUID() {
        return tribeMemberUUID;
    }

    public String getTribeMemberName() {
        return tribeMemberName;
    }

    public Date getTribeMemberJoinDate() {
        return tribeMemberJoinDate;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("tribeMemberUUID", tribeMemberUUID);
        nbt.putString("tribeMemberName", tribeMemberName);
        nbt.putLong("tribeMemberJoinDate", tribeMemberJoinDate.getTime());
        return nbt;
    }

    public static TribeMember fromNbt(NbtCompound nbt) {
        TribeMember tribeMember = new TribeMember();
        tribeMember.tribeMemberUUID = nbt.getUuid("tribeMemberUUID");
        tribeMember.tribeMemberName = nbt.getString("tribeMemberName");
        tribeMember.tribeMemberJoinDate = new Date(nbt.getLong("tribeMemberJoinDate"));
        return tribeMember;
    }
}
