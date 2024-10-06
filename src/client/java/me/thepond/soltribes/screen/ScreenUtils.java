package me.thepond.soltribes.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ScreenUtils {

    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawCenteredRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x - width / 2, y - height / 2, x + width / 2, y + height / 2, color);
    }

    public static void drawScaledText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1);
        context.drawText(textRenderer, text, 0, 0, color, shadow);
        context.getMatrices().pop();
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
        context.drawText(textRenderer, text, x - textRenderer.getWidth(text) / 2, y - textRenderer.fontHeight / 2, color, shadow);
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1);
        context.drawText(textRenderer, text, -textRenderer.getWidth(text) / 2, -textRenderer.fontHeight / 2, color, shadow);
        context.getMatrices().pop();
    }

}
