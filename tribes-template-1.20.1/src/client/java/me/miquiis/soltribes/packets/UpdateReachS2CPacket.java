package me.thepond.soltribes.packets;

import me.thepond.soltribes.SOLTribesClient;
import me.thepond.soltribes.screen.TribeTableActivationScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class UpdateReachS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        float reach = buf.readFloat();
        client.execute(() -> {
            SOLTribesClient.setBoostedReach(reach);
        });
    }

}
