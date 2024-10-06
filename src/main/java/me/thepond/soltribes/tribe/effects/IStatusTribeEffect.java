package me.thepond.soltribes.tribe.effects;

import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public interface IStatusTribeEffect extends ITribeEffect {

    StatusEffect getStatusEffect();

    @Override
    default void tick(TribeSettlement tribeSettlement, ServerWorld world) {
        for (TribeMember tribeMember : tribeSettlement.getTribeMembersInRange()) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeMember.getTribeMemberUUID());
            if (player != null) {
                player.addStatusEffect(new StatusEffectInstance(getStatusEffect(), -1, 1, false, false));
            }
        }
    }

    @Override
    default void untick(List<TribeMember> removed, TribeSettlement tribeSettlement, ServerWorld world) {
        for (TribeMember tribeMember : removed) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(tribeMember.getTribeMemberUUID());
            if (player != null) {
                player.removeStatusEffect(getStatusEffect());
            }
        }
    }
}
