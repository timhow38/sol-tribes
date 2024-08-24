package me.thepond.soltribes.packets;

import me.thepond.soltribes.renderer.TribeBannerRenderer;
import me.thepond.soltribes.tribe.Tribe;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionVisitS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        VisitType visitType = buf.readEnumConstant(VisitType.class);
        if (visitType == VisitType.ENTER) {
            List<Tribe> tribes = buf.readCollection(ArrayList::new, byteBuf -> Tribe.fromNbt(byteBuf.readNbt()));
            client.execute(() -> {
                TribeBannerRenderer.setTribesInRange(tribes);
            });
        } else if (visitType == VisitType.LEAVE) {
            client.execute(TribeBannerRenderer::clearTribesInRange);
        } else if (visitType == VisitType.UPDATE) {
            UUID tribeId = buf.readUuid();
            NbtCompound bannerNbt = buf.readNbt();
            client.execute(() -> {
                TribeBannerRenderer.updateTribeInRange(tribeId, bannerNbt);
            });
        } else if (visitType == VisitType.ADD) {
            Tribe tribe = Tribe.fromNbt(buf.readNbt());
            client.execute(() -> {
                TribeBannerRenderer.addTribeInRange(tribe);
            });
        } else if (visitType == VisitType.REMOVE) {
            UUID tribeId = buf.readUuid();
            client.execute(() -> {
                TribeBannerRenderer.removeTribeInRange(tribeId);
            });
        }
    }

}
