package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

public class JumpBoostEffect implements IStatusTribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/jump_boost.png");

    @Override
    public int getTribeEffectId() {
        return 2;
    }

    @Override
    public String getTribeEffectName() {
        return "High Jump";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get receive jump boost effect.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.JUMP_BOOST;
    }
}
