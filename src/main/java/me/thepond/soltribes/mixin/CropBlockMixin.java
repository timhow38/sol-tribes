package me.thepond.soltribes.mixin;

import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.tribe.effects.TribeEffects;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {

    @Shadow public abstract int getAge(BlockState state);

    @Shadow public abstract int getMaxAge();

    @Shadow public abstract BlockState withAge(int age);

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        TribesData tribesData = TribesDataManager.getTribesData(world);
        for (Tribe tribe : tribesData.getNearbyTribesFromSettlement(pos)) {
            if (tribe != null && TribeEffects.isOf(TribeEffects.CROPS, tribe.getTribeActiveEffect())) {
                ci.cancel();
                if (world.getBaseLightLevel(pos, 0) >= 9) {
                    int i = this.getAge(state);
                    if (i < this.getMaxAge()) {
                        world.setBlockState(pos, this.withAge(i + 1), 2);
                        break;
                    }
                }
            }
        }
    }

}
