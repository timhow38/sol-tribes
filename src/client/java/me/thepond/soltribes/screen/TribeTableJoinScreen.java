package me.thepond.soltribes.screen;

import me.thepond.soltribes.SOLTribesServerConfig;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screenhandler.TribeTableJoinScreenHandlerBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TribeTableJoinScreen extends HandledScreen<TribeTableJoinScreenHandlerBlockEntity> {

    private ButtonWidget joinButton;

    private boolean init;

    public TribeTableJoinScreen(TribeTableJoinScreenHandlerBlockEntity handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.init = true;
        this.joinButton = ButtonWidget.builder(Text.literal("\u00A7f+"), this::onJoinPressed).dimensions(this.width / 2 - 10, this.height / 2 - 5, 20, 20).build();
        this.addDrawableChild(this.joinButton);
        this.joinButton.active = false;
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!init) return;
        this.renderBackground(context);
        this.drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        drawCenteredRect(context, this.width / 2, this.height / 2, 250, 100, 0xFFbcbcbe);
        context.drawText(textRenderer, "Tribe Table Join", this.width / 2 - textRenderer.getWidth("Tribe Table Join") / 2, this.height / 2 - 50 + 10, 0xFFFFFF, true);

        drawCenteredRect(context, this.width / 2, this.height / 2 + 5, 40, 40, 0xFF939393);

        joinButton.active = this.client.player.totalExperience >= 300;

        if (joinButton.isHovered()) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.literal("\u00A7eRequires \u00A7a%s XP\u00A7e points.".formatted(SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST, 300))));
            tooltip.add(client.player.totalExperience >= SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST, 300) ? Text.literal("Click to activate") : Text.literal("Not enough XP points"));
            context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void close() {
        super.close();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.handler.blockEntity.getPos());
        ClientPlayNetworking.send(ModPackets.CLOSE_TRIBE_TABLE, buf);
    }

    private void onJoinPressed(ButtonWidget button) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.handler.blockEntity.getPos());
        ClientPlayNetworking.send(ModPackets.JOIN_PLAYER_AT_TRIBE_TABLE, buf);
    }

    private void drawCenteredRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x - width / 2, y - height / 2, x + width / 2, y + height / 2, color);
    }
}
