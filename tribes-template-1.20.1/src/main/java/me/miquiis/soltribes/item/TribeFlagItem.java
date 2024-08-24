package me.thepond.soltribes.item;

import me.thepond.soltribes.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TribeFlagItem extends BlockItem {

    public TribeFlagItem(Settings settings) {
        super(ModBlocks.TRIBE_FLAG, settings);
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        ItemStack itemStack = context.getStack();

        if (!itemStack.hasNbt() || !itemStack.getNbt().contains("TribeId")) {
            return false;
        }

        return super.canPlace(context, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains("SettlementName")) {
            tooltip.add(Text.of("Settlement: " + nbt.getString("SettlementName")));
        }
        if (nbt.contains("SettlementDescription")) {
            tooltip.add(Text.of("Description: " + nbt.getString("SettlementDescription")));
        }
    }
}
