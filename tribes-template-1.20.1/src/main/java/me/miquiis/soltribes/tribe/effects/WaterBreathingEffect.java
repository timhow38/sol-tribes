package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

public class WaterBreathingEffect implements IStatusTribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/water_breathing.png");

    @Override
    public int getTribeEffectId() {
        return 7;
    }

    @Override
    public String getTribeEffectName() {
        return "Extra Breath";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get receive water breathing effect.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.WATER_BREATHING;
    }
}
