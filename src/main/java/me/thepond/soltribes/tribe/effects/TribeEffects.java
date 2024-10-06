package me.thepond.soltribes.tribe.effects;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TribeEffects {

    private static final List<ITribeEffect> TRIBE_EFFECTS = new ArrayList<>();

    public static final LessHungerTribeEffect LESS_HUNGER = register(new LessHungerTribeEffect());
    public static final JumpBoostEffect JUMP_BOOST = register(new JumpBoostEffect());
    public static final NightVisionEffect NIGHT_VISION = register(new NightVisionEffect());
    public static final SlowFallingEffect SLOW_FALLING = register(new SlowFallingEffect());
    public static final RegenerationEffect REGENERATION = register(new RegenerationEffect());
    public static final SpeedEffect SPEED = register(new SpeedEffect());
    public static final WaterBreathingEffect WATER_BREATHING = register(new WaterBreathingEffect());
    public static final MagnetEffect MAGNET = register(new MagnetEffect());
    public static final NoEffectsEffect NO_EFFECTS = register(new NoEffectsEffect());
    public static final BlockReachEffect BLOCK_REACH = register(new BlockReachEffect());
    public static final CropsEffect CROPS = register(new CropsEffect());

    private static <T extends ITribeEffect> T register(T effect) {
        TRIBE_EFFECTS.add(effect);
        return effect;
    }

    public static List<ITribeEffect> getTribeEffects() {
        return TRIBE_EFFECTS;
    }

    public static ITribeEffect getTribeEffectById(int id) {
        return TRIBE_EFFECTS.stream().filter(effect -> effect.getTribeEffectId() == id).findFirst().orElse(null);
    }

    public static boolean isOf(@Nullable ITribeEffect effect, @Nullable ITribeEffect effect2) {
        if (effect == null || effect2 == null) {
            return false;
        }
        return effect.getTribeEffectId() == effect2.getTribeEffectId();
    }
}
