package me.thepond.soltribes.screen.widget;

import me.thepond.soltribes.SOLTribesClient;
import me.thepond.soltribes.renderer.TribeBannerRenderer;
import me.thepond.soltribes.screen.TribeInformationScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TribeInfoWidget extends ButtonWidget {

    private MinecraftClient client;
    private boolean open;

    public TribeInfoWidget(int x, int y, int width, int height, boolean open) {
        super(x, y, width, height, Text.empty(), null, Supplier::get);
        this.client = MinecraftClient.getInstance();
        this.open = open;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (SOLTribesClient.getCurrentTribe() != null) {
            playDownSound(client.getSoundManager());
            if (!open) {
                client.setScreen(new TribeInformationScreen());
            } else {
                client.setScreen(new InventoryScreen(client.player));
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (SOLTribesClient.getCurrentTribe() != null) {
            if (!open) context.fill(getX() - 1, getY() - 1, getX() + getWidth() + 1, getY() + getHeight() + 1, 0xFF000000);
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), !open ? !isHovered() ? 0xFF939393 : 0xFFB0B0B0 : !isHovered() ? 0xFFbcbcbe : 0xFFB0B0B0);
            TribeBannerRenderer.renderBanner(client, context, SOLTribesClient.getCurrentTribe(), getX() + 15, getY() + 6, 12);
            if (isHovered()) {
                List<Text> toolTip = new ArrayList<>();
                toolTip.add(Text.literal("Tribe: " + SOLTribesClient.getCurrentTribe().getTribeName()));
                toolTip.add(Text.literal(!open ? "Click to view more information" : "Click to return to inventory").formatted(Formatting.YELLOW));
                context.drawTooltip(client.textRenderer, toolTip, mouseX, mouseY);
            }
        }
    }
}
