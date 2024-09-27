package me.thepond.soltribes.packets;

import me.thepond.soltribes.SOLTribesServerConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class SyncConfigS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        String bufString = buf.readString();
        client.execute(() -> {
            SOLTribesServerConfig.readFromString(bufString);
        });
    }

}
