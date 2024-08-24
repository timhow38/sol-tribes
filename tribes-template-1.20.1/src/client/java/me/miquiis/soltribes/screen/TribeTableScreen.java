package me.thepond.soltribes.screen;

import com.mojang.datafixers.util.Pair;
import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screenhandler.ITribeTableBlockEntity;
import me.thepond.soltribes.tribe.Tribe;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public class TribeTableScreen<T extends ScreenHandler> extends HandledScreen<T> {

    protected final int TRIBE_TABLE_WIDTH = 250;
    protected final int TRIBE_TABLE_HEIGHT = 100;
    protected final int TRIBE_TABLE_PADDING = 8;

    private static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");
    private static final Identifier INFO_ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_tabs/info.png");
    private static final Identifier STATS_ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_tabs/stats.png");
    private static final Identifier FLAGS_ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_tabs/flags.png");
    private static final Identifier EFFECTS_ICON = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_tabs/effects.png");

    private TabButton infoButton;
    private TabButton flagsButton;
    private TabButton effectsButton;
    private TabButton statsButton;

    public TribeTableScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        int tabX = this.width / 2 - (TRIBE_TABLE_WIDTH / 2) - 20;
        this.infoButton = new TabButton(tabX, getTabY(0), 20, 20, Text.of("Info"), (buttonWidget) -> onTabPress(0, buttonWidget), Supplier::get);
        this.addSelectableChild(this.infoButton);
        this.flagsButton = new TabButton(tabX, getTabY(1), 20, 20, Text.of("Flags"), (buttonWidget) -> onTabPress(1, buttonWidget), Supplier::get);
        this.addSelectableChild(this.flagsButton);
        this.effectsButton = new TabButton(tabX, getTabY(2), 20, 20, Text.of("Effects"), (buttonWidget) -> onTabPress(2, buttonWidget), Supplier::get);
        this.addSelectableChild(this.effectsButton);
        this.statsButton = new TabButton(tabX, getTabY(3), 20, 20, Text.of("Stats"), (buttonWidget) -> onTabPress(3, buttonWidget), Supplier::get);
        this.statsButton.active = false;
        this.addSelectableChild(this.statsButton);
    }

    private int getTabY(int index) {
        return this.height / 2 - 50 + 13 + 22 * index;
    }

    private void onTabPress(int index, ButtonWidget buttonWidget) {
        save();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(getTribeTableBlockEntity().getPos());
        buf.writeInt(index);
        ClientPlayNetworking.send(ModPackets.OPEN_TAB_TRIBE_TABLE, buf);
    }

    protected void save() {}

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {

    }

    @Override
    public void close() {
        save();
        super.close();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(getTribeTableBlockEntity().getPos());
        ClientPlayNetworking.send(ModPackets.CLOSE_TRIBE_TABLE, buf);
    }

    protected int getTabIndex() {
        return 0;
    }

    protected void drawTribeBackground(DrawContext context, int mouseX, int mouseY) {
        this.renderBackground(context);
        ScreenUtils.drawCenteredRect(context, this.width / 2, this.height / 2, TRIBE_TABLE_WIDTH, TRIBE_TABLE_HEIGHT, 0xFFbcbcbe);
        ScreenUtils.drawRect(context, this.width / 2 - (TRIBE_TABLE_WIDTH / 2), this.height / 2 - 50, TRIBE_TABLE_WIDTH, 12, 0xFF939393);

        context.drawText(textRenderer, getTitle(), this.width / 2 - (TRIBE_TABLE_WIDTH / 2) + TRIBE_TABLE_PADDING, this.height / 2 - 50 + textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
        Tribe tribe = getTribeTableBlockEntity().getTribe();
        if (tribe != null) {
            String tribeName = tribe.getTribeName();
            int textWidth = textRenderer.getWidth(tribeName);
            context.drawText(textRenderer, tribeName, this.width / 2 + (TRIBE_TABLE_WIDTH / 2) - textWidth - TRIBE_TABLE_PADDING, this.height / 2 - 50 + textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
            this.renderBanner(context, this.width / 2 + (TRIBE_TABLE_WIDTH / 2) - textWidth - TRIBE_TABLE_PADDING - 5, this.height / 2 - 50 + 1, 4);
            ScreenUtils.drawRect(context, this.width / 2 + (TRIBE_TABLE_WIDTH / 2) - textWidth - TRIBE_TABLE_PADDING - 12, this.height / 2 - 50 + 2, 2, 8, 0xFFb9b9ba);
        }

        for (int i = 0; i < 4; i++) {
            // Draw the tab
            int tabY = getTabY(i);
            boolean selected = i == this.getTabIndex();
            ScreenUtils.drawRect(context, this.width / 2 - (TRIBE_TABLE_WIDTH / 2) - 20, tabY, 20, 20, selected ? 0xFFbcbcbe : 0xFF929292);

            int size = 16;
            Identifier icon = i == 0 ? INFO_ICON : i == 3 ? STATS_ICON : i == 1 ? FLAGS_ICON : EFFECTS_ICON;
            context.drawTexture(icon, this.width / 2 - (TRIBE_TABLE_WIDTH / 2) - 20 + 2, tabY + 2, 0, 0, size, size, size, size);
        }

        if (shouldRenderHotbar()) {
            context.drawTexture(INVENTORY_TEXTURE, this.width / 2 - 162 / 2, this.height / 2 + 58, 7, 141, 162, 18);
        }
    }

    protected void renderBanner(DrawContext context, int x, int y, int size) {
        Tribe tribe = getTribeTableBlockEntity().getTribe();
        if (tribe != null) {
            if (!tribe.getTribeBanner().isEmpty()) {
                DyeColor baseColor = DyeColor.byId(tribe.getTribeBanner().getInt("Color"));
                List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = BannerBlockEntity.getPatternsFromNbt(baseColor, tribe.getTribeBanner().getList("Patterns", 10));
                this.renderBanner(context.getMatrices(), patterns, x, y, size);
            }
        }
    }

    private void renderBanner(MatrixStack matrices, List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns, int x, int y, int size) {
        if (patterns.isEmpty()) return;

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(size, size, -1.0F);

        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        ModelPart modelPart = client.getEntityModelLoader().getModelPart(EntityModelLayers.BANNER);

        for(int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<RegistryEntry<BannerPattern>, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            pair.getFirst().getKey().map(TexturedRenderLayers::getBannerPatternTextureId).ifPresent(spriteIdentifier -> {
                modelPart.render(matrices, spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), 15728880, OverlayTexture.DEFAULT_UV, fs[0], fs[1], fs[2], 1.0F);
            });
        }

        vertexConsumers.draw();
        matrices.pop();
    }

    protected boolean shouldRenderHotbar() {
        return true;
    }

    protected TribeTableBlockEntity getTribeTableBlockEntity() {
        return ((ITribeTableBlockEntity)this.handler).getTribeTableBlockEntity();
    }

    private static class TabButton extends ButtonWidget {

        public TabButton(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
            super(x, y, width, height, message, onPress, narrationSupplier);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
//            super.renderButton(context, mouseX, mouseY, delta);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
        }
    }
}
