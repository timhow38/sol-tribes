package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

public class SlowFallingEffect implements IStatusTribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/slow_fall.png");

    @Override
    public int getTribeEffectId() {
        return 4;
    }

    @Override
    public String getTribeEffectName() {
        return "Feather Fall";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get receive slow falling effect.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.SLOW_FALLING;
    }
}
