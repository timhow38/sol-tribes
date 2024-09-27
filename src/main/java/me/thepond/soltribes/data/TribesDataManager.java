package me.thepond.soltribes.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class TribesDataManager {

    private static final String TRIBES_DATA_NAME = "tribes_data";

    public static TribesData getTribesData(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        PersistentStateManager stateManager = world.getPersistentStateManager();
        return stateManager.getOrCreate(TribesData::fromNbt, TribesData::new, TRIBES_DATA_NAME);
    }

    public static TribesData getTribesData(ServerWorld world) {
        PersistentStateManager stateManager = world.getPersistentStateManager();
        return stateManager.getOrCreate(TribesData::fromNbt, TribesData::new, TRIBES_DATA_NAME);
    }

}
