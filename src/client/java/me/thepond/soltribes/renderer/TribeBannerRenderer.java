package me.thepond.soltribes.renderer;

import com.mojang.datafixers.util.Pair;
import me.thepond.solregions.SOLRegionsConfig;
import me.thepond.solregions.data.Region;
import me.thepond.solregions.events.RenderRegionEvent;
import me.thepond.solregions.screens.SOLRegionsOptionsScreen;
import me.thepond.soltribes.tribe.Tribe;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TribeBannerRenderer implements RenderRegionEvent.Text {

    private static final List<Tribe> TRIBES_IN_RANGE = new ArrayList<>();

    public static void addTribeInRange(Tribe tribe) {
        TRIBES_IN_RANGE.add(tribe);
    }

    public static void setTribesInRange(List<Tribe> tribes) {
        TRIBES_IN_RANGE.clear();
        TRIBES_IN_RANGE.addAll(tribes);
    }

    public static void removeTribeInRange(Tribe tribe) {
        TRIBES_IN_RANGE.remove(tribe);
    }

    public static void removeTribeInRange(UUID tribeUUID) {
        TRIBES_IN_RANGE.removeIf(tribe -> tribe.getTribeId().equals(tribeUUID));
    }

    public static void clearTribesInRange() {
        TRIBES_IN_RANGE.clear();
    }

    public static void updateTribeInRange(UUID tribeUUID, NbtCompound bannerNbt) {
        if (bannerNbt != null) {
            TRIBES_IN_RANGE.stream()
                    .filter(tribe -> tribe.getTribeId().equals(tribeUUID))
                    .findFirst()
                    .ifPresent(tribe -> tribe.setTribeBanner(bannerNbt));
        }
    }

    @Override
    public void onText(DrawContext drawContext, Region region) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        boolean isInventoryOpen = client.currentScreen instanceof AbstractInventoryScreen<?>;
        int textPositionIndex = SOLRegionsConfig.getInt(isInventoryOpen ? "inventoryOpenedPosition" : "defaultPosition", 0);
        int nextX = 0;
        for (Tribe tribe : TRIBES_IN_RANGE) {
            if (!tribe.getTribeBanner().isEmpty()) {
                boolean isLeft = textPositionIndex == 1 || textPositionIndex == 2;
                boolean isBottom = textPositionIndex > 1;
                int scaledTitleX = -textRenderer.getWidth(region.getRegionName()) + 3;
                renderBanner(client, drawContext, tribe, isLeft ? nextX + 3 : -scaledTitleX -nextX, isBottom ? - 13 : 10, 4);
                nextX += 6;
            }
        }
    }

    public static void renderBanner(MinecraftClient client, DrawContext context, Tribe tribe, int x, int y, int size) {
        if (tribe != null) {
            if (!tribe.getTribeBanner().isEmpty()) {
                DyeColor baseColor = DyeColor.byId(tribe.getTribeBanner().getInt("Color"));
                List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = BannerBlockEntity.getPatternsFromNbt(baseColor, tribe.getTribeBanner().getList("Patterns", 10));
                TribeBannerRenderer.renderBanner(client, context.getMatrices(), patterns, x, y, size);
            }
        }
    }

    public static void renderBanner(MinecraftClient client, MatrixStack matrices, List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns, int x, int y, int size) {
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
}
