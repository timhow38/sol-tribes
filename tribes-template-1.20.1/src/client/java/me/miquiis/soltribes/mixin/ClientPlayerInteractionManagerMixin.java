package me.thepond.soltribes.mixin;

import me.thepond.soltribes.SOLTribesClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    private void getReachDistance(CallbackInfoReturnable<Float> cir) {
        if (SOLTribesClient.getBoostedReach() != -1F) {
            cir.setReturnValue(SOLTribesClient.getBoostedReach());
        }
    }
}
