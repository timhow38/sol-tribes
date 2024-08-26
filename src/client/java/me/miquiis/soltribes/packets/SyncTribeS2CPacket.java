package me.thepond.soltribes.packets;

import me.thepond.soltribes.SOLTribesClient;
import me.thepond.soltribes.tribe.Tribe;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class SyncTribeS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        try {
            NbtCompound nbt = buf.readNbt();
            client.execute(() -> SOLTribesClient.setCurrentTribe(nbt == null ? null : Tribe.fromNbt(nbt)));
        } catch (Exception e) {
            client.execute(() -> SOLTribesClient.setCurrentTribe(null));
        }
    }

}
