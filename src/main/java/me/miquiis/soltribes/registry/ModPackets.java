package me.thepond.soltribes.registry;

import me.thepond.soltribes.packets.*;
import me.thepond.soltribes.SOLTribes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {

    // Server to client packets.
    public static final Identifier UPDATE_REACH = new Identifier(SOLTribes.MOD_ID, "update_reach");
    public static final Identifier REGION_VISIT = new Identifier(SOLTribes.MOD_ID, "region_visit");
    public static final Identifier SYNC_TRIBE = new Identifier(SOLTribes.MOD_ID, "sync_tribe");
    public static final Identifier SYNC_CONFIG = new Identifier(SOLTribes.MOD_ID, "sync_config");

    // Client to server packets.
    public static final Identifier ACTIVE_PLAYER_AT_TRIBE_TABLE = new Identifier(SOLTribes.MOD_ID, "activate_player_at_tribe_table");
    public static final Identifier JOIN_PLAYER_AT_TRIBE_TABLE = new Identifier(SOLTribes.MOD_ID, "join_player_at_tribe_table");
    public static final Identifier CLOSE_TRIBE_TABLE = new Identifier(SOLTribes.MOD_ID, "close_tribe_table");
    public static final Identifier OPEN_TAB_TRIBE_TABLE = new Identifier(SOLTribes.MOD_ID, "open_table_tribe_table");
    public static final Identifier UPDATE_TRIBE = new Identifier(SOLTribes.MOD_ID, "update_tribe");
    public static final Identifier CREATE_SETTLEMENT_FLAG = new Identifier(SOLTribes.MOD_ID, "create_settlement_flag");
    public static final Identifier LEAVE_TRIBE = new Identifier(SOLTribes.MOD_ID, "leave_tribe");

    public static void registerC2SPackets() {
        SOLTribes.LOGGER.debug("Registering C2S packets...");
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_PLAYER_AT_TRIBE_TABLE, (server, player, handler, buf, responseSender) -> {
            ActivatePlayerAtTribeTableC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(JOIN_PLAYER_AT_TRIBE_TABLE, (server, player, handler, buf, responseSender) -> {
            JoinPlayerAtTribeTableC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(CLOSE_TRIBE_TABLE, (server, player, handler, buf, responseSender) -> {
            CloseTribeTableC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(OPEN_TAB_TRIBE_TABLE, (server, player, handler, buf, responseSender) -> {
            OpenTabTribeTableC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_TRIBE, (server, player, handler, buf, responseSender) -> {
            UpdateTribeC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(CREATE_SETTLEMENT_FLAG, (server, player, handler, buf, responseSender) -> {
            CreateSettlementFlagC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(LEAVE_TRIBE, (server, player, handler, buf, responseSender) -> {
            LeaveTribeC2SPacket.receive(server, handler, buf, responseSender);
        });
    }

}
