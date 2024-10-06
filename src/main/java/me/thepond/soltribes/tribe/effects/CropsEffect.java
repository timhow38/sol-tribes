package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.tribe.TribeSettlement;
import me.thepond.soltribes.SOLTribes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class CropsEffect implements ITribeEffect {

    private static final Identifier ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_effects/crops.png");

    @Override
    public int getTribeEffectId() {
        return 11;
    }

    @Override
    public String getTribeEffectName() {
        return "Growth Speed";
    }

    @Override
    public String getTribeEffectDescription() {
        return "Nearby crops will grow faster.";
    }

    @Override
    public Identifier getTribeEffectIcon() {
        return ICON;
    }

    @Override
    public void tick(TribeSettlement tribeSettlement, ServerWorld world) {
    }
}
