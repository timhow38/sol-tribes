package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LessHungerTribeEffect implements ITribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/less_hunger.png");

    @Override
    public int getTribeEffectId() {
        return 1;
    }

    @Override
    public String getTribeEffectName() {
        return "Less Hunger";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get hungry less often.";
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
                player.getHungerManager().setExhaustion(player.getHungerManager().getExhaustion() - 0.01f);
            }
        }
    }
}
