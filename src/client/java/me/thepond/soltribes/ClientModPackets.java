package me.thepond.soltribes;

import me.thepond.soltribes.packets.RegionVisitS2CPacket;
import me.thepond.soltribes.packets.SyncConfigS2CPacket;
import me.thepond.soltribes.packets.SyncTribeS2CPacket;
import me.thepond.soltribes.packets.UpdateReachS2CPacket;
import me.thepond.soltribes.registry.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModPackets {
    public static void registerS2CPackets() {
        SOLTribes.LOGGER.debug("Registering S2C packets...");
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_REACH, UpdateReachS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.REGION_VISIT, RegionVisitS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_TRIBE, SyncTribeS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_CONFIG, SyncConfigS2CPacket::receive);
    }
}
