package me.thepond.soltribes.command;

import com.mojang.brigadier.CommandDispatcher;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModCommands implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("tribes")
                .then(CommandManager.literal("leave").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    TribesData tribeData = TribesDataManager.getTribesData(player.getServerWorld());
                    Tribe tribe = tribeData.getTribeFromPlayer(player.getUuid());
                    if (tribe != null) {
                        System.out.println("Leaving tribe");
                        tribe.removeTribeMember(player.getUuid());
                        tribeData.setDirty(true);
                        tribe.sendTableUpdate(player.getServerWorld());
                    }
                    return 1;
                }))
                .then(CommandManager.literal("disband").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    TribesData tribeData = TribesDataManager.getTribesData(player.getServerWorld());
                    Tribe tribe = tribeData.getTribeFromPlayer(player.getUuid());
                    if (tribe != null && tribe.isFounder(player.getUuid())) {
                        System.out.println("Disbanding tribe");
                        tribe.setDisbanded(true);
                        tribeData.setDirty(true);
                        tribe.sendTableUpdate(player.getServerWorld());
                    }
                    return 1;
                }))
        );
    }
}
