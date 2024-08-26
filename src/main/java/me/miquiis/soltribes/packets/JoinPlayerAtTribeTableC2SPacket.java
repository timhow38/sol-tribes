package me.thepond.soltribes.packets;

import me.thepond.soltribes.SOLTribesServerConfig;
import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class JoinPlayerAtTribeTableC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ServerPlayerEntity player = handler.player;
        BlockPos pos = buf.readBlockPos();
        server.execute(() -> {
            if (player.getWorld().getBlockEntity(pos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
                if (player.totalExperience >= SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST, 300)) {
                    if (tribeTableBlockEntity.addTribeMember(player.getUuid())) {
                        player.addExperience(-SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST, 300));
                    }
                }
            }
        });
    }
}
