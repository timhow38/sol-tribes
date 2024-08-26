package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.SOLTribes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

public class NightVisionEffect implements IStatusTribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/night_vision.png");

    @Override
    public int getTribeEffectId() {
        return 3;
    }

    @Override
    public String getTribeEffectName() {
        return "Night Vision";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Members of this tribe will get receive night vision effect.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.NIGHT_VISION;
    }
}
