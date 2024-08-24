package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class MagnetEffect implements ITribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/magnet.png");

    @Override
    public int getTribeEffectId() {
        return 8;
    }

    @Override
    public String getTribeEffectName() {
        return "Magnet Pickup";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will pickup items around them from far away.";
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
                world.getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(5.0D, 5.0D, 5.0D), entity -> true).forEach(itemEntity -> {
                    if (itemEntity.isAlive()) {
                        itemEntity.teleport(player.getX(), player.getY(), player.getZ());
                        itemEntity.setPickupDelay(0);
                    }
                });
            }
        }
    }
}
