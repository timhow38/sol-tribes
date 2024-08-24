package me.thepond.soltribes.packets;

import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.tribe.effects.TribeEffects;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class UpdateTribeC2SPacket {

    public enum UpdateType {
        NAME,
        DESCRIPTION,
        DISCORD_URL,
        INFORMATION,
        EFFECT
    }

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UpdateType type = buf.readEnumConstant(UpdateType.class);
        switch (type) {
            case NAME -> updateTribeName(server, handler, buf, responseSender);
            case DESCRIPTION -> updateTribeDescription(server, handler, buf, responseSender);
            case DISCORD_URL -> updateTribeDiscordUrl(server, handler, buf, responseSender);
            case INFORMATION -> updateTribeInformation(server, handler, buf, responseSender);
            case EFFECT -> updateTribeEffect(server, handler, buf, responseSender);
        }
    }

    private static void updateTribeName(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String name = buf.readString();
    }

    private static void updateTribeDescription(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String description = buf.readString();
    }

    private static void updateTribeDiscordUrl(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String discordUrl = buf.readString();
    }

    private static void updateTribeInformation(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid();
        String name = buf.readString();
        String description = buf.readString();
        String discordUrl = buf.readString();

        server.execute(() -> {
            TribesData tribesData = TribesDataManager.getTribesData(server);

            Tribe tribe = tribesData.getTribe(uuid);
            tribe.setTribeName(name);
            tribe.setTribeDescription(description);
            tribe.setTribeDiscordUrl(discordUrl);

            tribesData.setDirty(true);

            updateTribe(server, tribe);
        });
    }

    private static void updateTribeEffect(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid();
        int effectId = buf.readInt();
        server.execute(() -> {
            TribesData tribesData = TribesDataManager.getTribesData(server);

            Tribe tribe = tribesData.getTribe(uuid);
            tribe.setTribeActiveEffect(TribeEffects.getTribeEffectById(effectId));

            tribesData.setDirty(true);

            updateTribe(server, tribe);
        });
    }

    private static void updateTribe(MinecraftServer server, Tribe tribe) {
        if (tribe.getTribeTablePos() != null) {
            for (ServerWorld world : server.getWorlds()) {
                if (world.getBlockEntity(tribe.getTribeTablePos()) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
                    if (tribeTableBlockEntity.getTribe().getTribeId().equals(tribe.getTribeId())) {
                        tribeTableBlockEntity.updateTribe(tribe);
                    }
                }
            }
        }
    }

}
