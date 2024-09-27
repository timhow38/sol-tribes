package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class NoEffectsEffect implements ITribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/no_effects.png");

    @Override
    public int getTribeEffectId() {
        return 9;
    }

    @Override
    public String getTribeEffectName() {
        return "Effect Removal";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will no longer get affected by any effects.";
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
                player.clearStatusEffects();
            }
        }
    }
}
