package me.thepond.soltribes.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class SimpleButtonWidget extends ButtonWidget {

    private int borderColor;
    private int backgroundColor;

    public SimpleButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, int borderColor, int backgroundColor) {
        super(x, y, width, height, message, onPress, Supplier::get);
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.renderButton(context, mouseX, mouseY, delta);\

        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, this.borderColor);
        context.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, this.backgroundColor);

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.drawText(minecraftClient.textRenderer, this.getMessage(), this.getX() + this.width / 2 - minecraftClient.textRenderer.getWidth(this.getMessage()) / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFF, false);
    }
}
