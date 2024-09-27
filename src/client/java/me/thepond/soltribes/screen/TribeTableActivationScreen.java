package me.thepond.soltribes.screen;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.SOLTribesClient;
import me.thepond.soltribes.SOLTribesClientConfig;
import me.thepond.soltribes.SOLTribesServerConfig;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screenhandler.TribeTableActivationScreenHandlerBlockEntity;
import me.thepond.soltribes.utils.PlayerInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TribeTableActivationScreen extends HandledScreen<TribeTableActivationScreenHandlerBlockEntity> {

    private List<ButtonWidget> activateButtons;

    private List<ItemStack> playerHeads;

    private boolean init;

    public TribeTableActivationScreen(TribeTableActivationScreenHandlerBlockEntity handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.init = true;

        this.activateButtons = new ArrayList<>();
        this.playerHeads = new ArrayList<>();

        this.activateButtons.add(ButtonWidget.builder(Text.literal("\u00A7f+"), button -> onActivatePressed(button, 0)).size(20, 20).build());
        this.activateButtons.add(ButtonWidget.builder(Text.literal("\u00A7f+"), button -> onActivatePressed(button, 1)).size(20, 20).build());
        this.activateButtons.add(ButtonWidget.builder(Text.literal("\u00A7f+"), button -> onActivatePressed(button, 2)).size(20, 20).build());
        this.activateButtons.forEach(buttonWidget -> {
            this.addDrawableChild(buttonWidget);
            buttonWidget.active = false;
            buttonWidget.visible = false;
        });

        this.handler.blockEntity.getTribeFounders().forEach(playerInfo -> {
            ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
            playerHead.getOrCreateNbt().putString("SkullOwner", playerInfo.getPlayerName());
            this.playerHeads.add(playerHead);
        });
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (!init) {
            return;
        }
        try {
            if (this.handler.blockEntity.getTribeFounders().size() > playerHeads.size()) {
                this.playerHeads.clear();
                for (PlayerInfo playerInfo : this.handler.blockEntity.getTribeFounders()) {
                    ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
                    playerHead.getOrCreateNbt().putString("SkullOwner", playerInfo.getPlayerName());
                    this.playerHeads.add(playerHead);
                }
            }
        } catch (Exception e) {
            SOLTribes.LOGGER.error("Error trying to tick activation screen: " + e.getMessage());
        }
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
        context.drawText(textRenderer, "Tribe Table Activation", this.width / 2 - textRenderer.getWidth("Tribe Table Activation") / 2, this.height / 2 - 50 + 10, 0xFFFFFF, true);

        int size = 40;
        int numElements = 3;
        int totalWidth = 250;

        int occupiedWidth = size * numElements;
        int remainingWidth = totalWidth - occupiedWidth;
        int gap = remainingWidth / (numElements + 1);

        for (int i = 0; i < numElements; i++) {
            // Calculate the x position for each element
            int xPos = this.width / 2 - totalWidth / 2 + gap * (i + 1) + size * i;

            // Draw the centered rectangle
            drawCenteredRect(context, xPos + size / 2, this.height / 2 + 5, size, size, 0xFF939393);

            if (this.handler.blockEntity.getTribeFounders().size() > i) {
                PlayerInfo playerInfo = this.handler.blockEntity.getTribeFounders().get(i);
                ButtonWidget button = this.activateButtons.get(i);
                button.active = false;
                button.visible = false;
                if (this.playerHeads.size() > i) {
                    ItemStack playerHead = this.playerHeads.get(i);
                    context.drawItem(playerHead, xPos + size / 2 - 8, this.height / 2 + 5 - 8);
                    context.drawText(textRenderer, playerInfo.getPlayerName(), xPos + size / 2 - textRenderer.getWidth(playerInfo.getPlayerName()) / 2, this.height / 2 + 5 + size / 2 + 4, 0x404040, false);
                }
            } else {
                String text = "Empty";
                context.drawText(textRenderer, text, xPos + size / 2 - textRenderer.getWidth(text) / 2, this.height / 2 + 5 + size / 2 + 4, 0x525252, false);
                ButtonWidget button = this.activateButtons.get(i);
                button.setPosition(xPos + size / 2 - button.getWidth() / 2, this.height / 2 + 5 - button.getHeight() / 2);

                boolean isAlreadyFounder = this.handler.blockEntity.getTribeFounders().stream().anyMatch(playerInfo -> playerInfo.getPlayerName().equals(this.client.player.getName().getString()));

                button.active = !isAlreadyFounder && this.client.player.totalExperience >= 300;
                button.visible = true;

                if (button.isHovered()) {
                    List<Text> tooltip = new ArrayList<>();
                    if (!isAlreadyFounder) {
                        tooltip.add(Text.literal("\u00A7eRequires \u00A7a%s XP\u00A7e points.".formatted(SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST, 300))));
                        tooltip.add(client.player.totalExperience >= SOLTribesServerConfig.getInt(SOLTribesServerConfig.ACTIVATION_COST, 300) ? Text.literal("Click to activate") : Text.literal("Not enough XP points"));
                    } else {
                        tooltip.add(Text.literal("\u00A7eYou've already applied."));
                    }
                    context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void close() {
        super.close();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.handler.blockEntity.getPos());
        ClientPlayNetworking.send(ModPackets.CLOSE_TRIBE_TABLE, buf);
    }

    private void onActivatePressed(ButtonWidget button, int index) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.handler.blockEntity.getPos());
        buf.writeInt(index);
        ClientPlayNetworking.send(ModPackets.ACTIVE_PLAYER_AT_TRIBE_TABLE, buf);
    }

    private void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    private void drawCenteredRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x - width / 2, y - height / 2, x + width / 2, y + height / 2, color);
    }
}
