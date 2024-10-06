package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public interface ITribeEffect {
    int getTribeEffectId();
    String getTribeEffectName();
    String getTribeEffectDescription();
    Identifier getTribeEffectIcon();
    void tick(TribeSettlement tribeSettlement, ServerWorld world);

    default void untick(List<TribeMember> removed, TribeSettlement tribeSettlement, ServerWorld world) {};
}
