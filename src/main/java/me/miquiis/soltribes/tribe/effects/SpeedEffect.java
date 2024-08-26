package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

public class SpeedEffect implements IStatusTribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/speed.png");

    @Override
    public int getTribeEffectId() {
        return 6;
    }

    @Override
    public String getTribeEffectName() {
        return "Speed Boost";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get receive speed effect.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.SPEED;
    }
}
