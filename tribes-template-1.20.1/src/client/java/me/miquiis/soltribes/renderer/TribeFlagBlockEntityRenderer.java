package me.thepond.soltribes.renderer;

import com.mojang.datafixers.util.Pair;
import me.thepond.soltribes.block.TribeFlagBlock;
import me.thepond.soltribes.block.entity.TribeFlagBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

public class TribeFlagBlockEntityRenderer implements BlockEntityRenderer<TribeFlagBlockEntity> {

    private final ModelPart banner;

    public TribeFlagBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.BANNER);
        this.banner = modelPart.getChild("flag");
    }

    @Override
    public void render(TribeFlagBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.getBannerNbt().isEmpty()) {
            DyeColor baseColor = DyeColor.byId(entity.getBannerNbt().getInt("Color"));
            List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = BannerBlockEntity.getPatternsFromNbt(baseColor, entity.getBannerNbt().getList("Patterns", 10));
            if (patterns.isEmpty()) return;
            BlockState blockState = entity.getCachedState();
            matrices.push();
            Direction direction = blockState.get(TribeFlagBlock.FACING);
            float h = -direction.asRotation();
            matrices.translate(direction == Direction.NORTH ? 0.5F : direction == Direction.SOUTH ? 0.5F : direction == Direction.WEST ? 0.3F : 0.7F, 0.66F, direction == Direction.EAST ? 0.5F : direction == Direction.WEST ? 0.5F : direction == Direction.NORTH ? 0.3F : 0.7F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
            matrices.push();
            matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
            BlockPos blockPos = entity.getPos();
            long l = entity.getWorld().getTime();
            float k = ((float)Math.floorMod((blockPos.getX() * 7L + blockPos.getY() * 9L + blockPos.getZ() * 13L) + l, 100L) + tickDelta) / 100.0F;
            this.banner.pitch = (-0.0125F + 0.01F * MathHelper.cos(6.2831855F * k)) * 3.1415927F;
            this.banner.pivotY = -32.0F;
            BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, this.banner, ModelLoader.BANNER_BASE, true, patterns);
            matrices.pop();
            matrices.pop();
        }
    }
}
