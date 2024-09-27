package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class RegenerationEffect implements ITribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/regeneration.png");

    @Override
    public int getTribeEffectId() {
        return 5;
    }

    @Override
    public String getTribeEffectName() {
        return "Health Regen";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get receive regeneration overtime.";
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
                player.heal(0.01f);
            }
        }
    }
}
