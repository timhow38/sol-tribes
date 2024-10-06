package me.thepond.soltribes.command;

import com.mojang.brigadier.CommandDispatcher;
import me.thepond.soltribes.SOLTribesServerConfig;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ModCommands implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("sol").then(CommandManager.literal("tribes")
                .then(CommandManager.literal("reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(context -> {
                    SOLTribesServerConfig.loadOptions();

                    System.out.println(SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST));

                    // Update all players with the new config
                    PacketByteBuf buf = PacketByteBufs.create();
                    SOLTribesServerConfig.writeToBuf(buf);
                    context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player -> {
                        ServerPlayNetworking.send(player, ModPackets.SYNC_CONFIG, buf);
                    });

                    context.getSource().sendFeedback(() -> Text.literal("SOL Tribes server config reloaded"), false);
                    return 1;
                }))
        ));
    }
}
