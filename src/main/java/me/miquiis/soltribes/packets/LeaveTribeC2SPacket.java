package me.thepond.soltribes.packets;

import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.tribe.Tribe;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class LeaveTribeC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            ServerPlayerEntity player = handler.getPlayer();
            TribesData tribeData = TribesDataManager.getTribesData(player.getServerWorld());
            Tribe tribe = tribeData.getTribeFromPlayer(player.getUuid());
            if (tribe != null) {
                tribe.removeTribeMember(player.getUuid());
                tribeData.setDirty(true);
                tribe.sendTableUpdate(player.getServerWorld());
            }
            ServerPlayNetworking.send(player, ModPackets.SYNC_TRIBE, PacketByteBufs.create());
        });
    }

}
