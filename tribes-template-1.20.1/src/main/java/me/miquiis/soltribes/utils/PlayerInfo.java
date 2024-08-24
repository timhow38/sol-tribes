package me.thepond.soltribes.utils;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class PlayerInfo {

    private String playerName;
    private UUID playerUUID;

    public PlayerInfo(String playerName, UUID playerUUID) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("playerName", playerName);
        nbt.putString("playerUUID", playerUUID.toString());
        return nbt;
    }

    public static PlayerInfo fromNbt(NbtCompound nbt) {
        return new PlayerInfo(nbt.getString("playerName"), UUID.fromString(nbt.getString("playerUUID")));
    }

}
