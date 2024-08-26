package me.thepond.soltribes.mixin;

import me.thepond.soltribes.screen.widget.TribeInfoWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    private TribeInfoWidget tribeInfoWidget;

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void onInit(CallbackInfo ci) {
        ScreenAccessor screenAccessor = (ScreenAccessor) this;
        InventoryScreen inventoryScreen = (InventoryScreen) (Object) this;
        int x = inventoryScreen.width / 2 - 176 / 2;
        int y = inventoryScreen.height / 2;
        tribeInfoWidget = new TribeInfoWidget(
                x - 30,
                y - 20,
                30,
                40,
                false
        );
        screenAccessor.invokeAddDrawableChild(tribeInfoWidget);
    }

    @Inject(
            method = "render",
            at = @At("RETURN")
    )
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    }

}
