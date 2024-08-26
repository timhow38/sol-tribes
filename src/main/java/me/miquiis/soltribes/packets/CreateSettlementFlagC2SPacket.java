package me.thepond.soltribes.packets;

import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class CreateSettlementFlagC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ServerPlayerEntity player = handler.player;
        BlockPos pos = buf.readBlockPos();
        String settlementName = buf.readString();
        String settlementDescription = buf.readString();
        server.execute(() -> {
            if (player.getWorld().getBlockEntity(pos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
                tribeTableBlockEntity.createSettlementFlag(player, settlementName, settlementDescription);
            }
        });
    }

}
