package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public class BlockReachEffect implements ITribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/reach.png");

    @Override
    public int getTribeEffectId() {
        return 10;
    }

    @Override
    public String getTribeEffectName() {
        return "Block Reach";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get an additional block reach.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public void tick(TribeSettlement tribeSettlement, ServerWorld world) {
        for (TribeMember tribeMember : tribeSettlement.getTribeMembersInRange()) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeMember.getTribeMemberUUID());
            if (player != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(5.5F);
                ServerPlayNetworking.send(player, ModPackets.UPDATE_REACH, buf);
            }
        }
    }

    @Override
    public void untick(List<TribeMember> removed, TribeSettlement tribeSettlement, ServerWorld world) {
        for (TribeMember tribeMember : removed) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeMember.getTribeMemberUUID());
            if (player != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(-1F);
                ServerPlayNetworking.send(player, ModPackets.UPDATE_REACH, buf);
            }
        }
    }
}
