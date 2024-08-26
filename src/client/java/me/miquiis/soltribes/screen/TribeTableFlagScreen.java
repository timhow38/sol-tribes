package me.thepond.soltribes.screen;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screenhandler.TribeTableFlagScreenHandlerBlockEntity;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class TribeTableFlagScreen extends TribeTableScreen<TribeTableFlagScreenHandlerBlockEntity> {

    private TextFieldWidget settlementNameField;
    private TextFieldWidget settlementDescriptionField;

    private ButtonWidget createSettlementButton;

    private SettlementList settlementList;

    private boolean init;

    public TribeTableFlagScreen(TribeTableFlagScreenHandlerBlockEntity handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.literal("SETTLEMENTS"));
    }

    @Override
    protected void init() {
        super.init();

        this.init = true;

        this.settlementNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 40, 90, 15, Text.of("Settlement Name"));
        this.settlementNameField.setMaxLength(32);
        this.settlementNameField.setDrawsBackground(false);
        this.addSelectableChild(this.settlementNameField);

        this.settlementDescriptionField = new TextFieldWidget(this.textRenderer, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 40 + 30, 90, 15, Text.of("Settlement Description"));
        this.settlementDescriptionField.setMaxLength(128);
        this.settlementDescriptionField.setDrawsBackground(false);
        this.addSelectableChild(this.settlementDescriptionField);

        this.createSettlementButton = ButtonWidget.builder(Text.literal("Create"), this::onCreateSettlementPress)
                .dimensions(this.width / 2 - 13, this.height / 2 + 20, 26, 10)
                .build();
        this.createSettlementButton.active = false;
        this.addSelectableChild(this.createSettlementButton);

        this.settlementList = new SettlementList(this.width / 2 + 25, this.height / 2 - 50 + 25, 90, 70, Text.of(""), textRenderer);
        this.addSelectableChild(this.settlementList);
    }

    private void onCreateSettlementPress(ButtonWidget button) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.getTribeTableBlockEntity().getPos());
        buf.writeString(this.settlementNameField.getText());
        buf.writeString(this.settlementDescriptionField.getText());
        ClientPlayNetworking.send(ModPackets.CREATE_SETTLEMENT_FLAG, buf);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (!init) {
            return;
        }
        try {
            this.settlementNameField.tick();
            this.settlementDescriptionField.tick();
            this.createSettlementButton.active = !this.settlementNameField.getText().isEmpty() && !this.settlementDescriptionField.getText().isEmpty() && !this.getTribeTableBlockEntity().getFlagsPageInventory().getStack(0).isEmpty();
        } catch (Exception e) {
            SOLTribes.LOGGER.error("Error trying to tick flag screen: " + e.getMessage());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
        }
        return this.settlementNameField.keyPressed(keyCode, scanCode, modifiers) || this.settlementNameField.isActive() || this.settlementDescriptionField.keyPressed(keyCode, scanCode, modifiers) || this.settlementDescriptionField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!init) return;
        super.drawTribeBackground(context, mouseX, mouseY);
//        context.drawText(textRenderer, "FLAGS CREATION", this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 20 - textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);

        ScreenUtils.drawCenteredText(context, textRenderer, "Capacity", this.width / 2, this.height / 2 - 18, 0xFFFFFF, false, 0.8f);
        ScreenUtils.drawCenteredText(context, textRenderer, String.valueOf(this.getTribeTableBlockEntity().getTribe().getSettlementCapacity()), this.width / 2, this.height / 2 - 10, 0xFFFFFF, false, 0.8f);
        ScreenUtils.drawCenteredRect(context, this.width / 2, this.height / 2 + 6, 25, 25, 0xFF939393);

        ScreenUtils.drawRect(context, settlementNameField.getX() - 2, settlementNameField.getY() - 2, settlementNameField.getWidth() + 2, 12, 0xFF939393);
        this.settlementNameField.render(context, mouseX, mouseY, delta);
        ScreenUtils.drawScaledText(context, textRenderer, "Name", settlementNameField.getX(), this.height / 2 - 50 + 40 - 10, 0xFFFFFF, false, 0.8f);
        ScreenUtils.drawRect(context, settlementDescriptionField.getX() - 2, settlementDescriptionField.getY() - 2, settlementDescriptionField.getWidth() + 2, 12, 0xFF939393);
        this.settlementDescriptionField.render(context, mouseX, mouseY, delta);
        ScreenUtils.drawScaledText(context, textRenderer, "Description", settlementDescriptionField.getX(), this.height / 2 - 50 + 40 + 30 - 10, 0xFFFFFF, false, 0.8f);

        context.drawText(textRenderer, "Settlement List", this.width / 2 + 25, this.height / 2 - 50 + 20 - textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
        ScreenUtils.drawRect(context, this.width / 2 + 25, this.height / 2 - 50 + 25, 90, 70, 0xFF939393);
        this.settlementList.render(context, mouseX, mouseY, delta);

        ScreenUtils.drawRect(context, this.width / 2 - 13, this.height / 2 + 20, 26, 10, !createSettlementButton.active ? 0x648f8f8f : 0xFF8f8f8f);
        ScreenUtils.drawCenteredText(context, textRenderer, "Create", this.width / 2, this.height / 2 + 25, 0xFFFFFF, false, 0.5f);

        super.render(context, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected int getTabIndex() {
        return 1;
    }

    private class SettlementList extends ScrollableWidget {

        private TextRenderer textRenderer;

        public SettlementList(int i, int j, int k, int l, Text text, TextRenderer textRenderer) {
            super(i, j, k, l, text);
            this.textRenderer = textRenderer;
        }

        @Override
        protected int getContentsHeight() {
            List<TribeSettlement> tribeSettlements = new ArrayList<>(TribeTableFlagScreen.this.getTribeTableBlockEntity().getTribe().getTribeSettlements());
            int height = tribeSettlements.size() * 35 - 2;
            return (int) Math.max(35, height);
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 8;
        }

        @Override
        protected void drawBox(DrawContext context) {
            super.drawBox(context);
        }

        @Override
        protected void drawBox(DrawContext context, int x, int y, int width, int height) {
//            super.drawBox(context, x, y, width, height);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        protected void renderOverlay(DrawContext context) {
            if (this.overflows()) {
                int i = MathHelper.clamp((int)((float)(this.height * this.height) / (float)this.getContentsHeight() + 4), 32, this.height);
                int j = this.getX() + this.width - 6;
                int k = this.getX() + this.width - 6 + 5;
                int l = Math.max(this.getY(), (int)this.getScrollY() * (this.height - i) / this.getMaxScrollY() + this.getY());
                int m = l + i;
                context.fill(j, l + 2, k, m - 2, -8355712);
                context.fill(j, l + 2, k - 1, m - 1 - 2, -4144960);
            }
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
        }

        @Override
        protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
            List<TribeSettlement> tribeSettlements = new ArrayList<>(TribeTableFlagScreen.this.getTribeTableBlockEntity().getTribe().getTribeSettlements());
            for (TribeSettlement tribeSettlement : tribeSettlements) {
                int x = this.getX() + 3;
                int y = this.getY() + 3 + tribeSettlements.indexOf(tribeSettlement) * 35;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd, yyyy");
                context.drawText(textRenderer, tribeSettlement.getTribeSettlementName(), x, y, 0xFFFFFF, false);
                ScreenUtils.drawRect(context, x, y + 10, 80, 1, 0xFFFFFFFF);
                ScreenUtils.drawScaledText(context, textRenderer, String.format("Coordinates: %s, %s, %s", tribeSettlement.getTribeSettlementPosition().getX(), tribeSettlement.getTribeSettlementPosition().getY(), tribeSettlement.getTribeSettlementPosition().getZ()), x, y + 15, 0xFFFFFF, false, 0.5f);
                ScreenUtils.drawScaledText(context, textRenderer, "Founded: " + dateFormat.format(tribeSettlement.getTribeSettlementCreationDate()), x, y + 22, 0xFFFFFF, false, 0.5f);
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
