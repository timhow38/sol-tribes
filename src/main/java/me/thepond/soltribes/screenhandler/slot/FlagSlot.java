package me.thepond.soltribes.screenhandler.slot;

import me.thepond.soltribes.registry.ModBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FlagSlot extends Slot {

    public FlagSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(ModBlocks.TRIBE_FLAG.asItem());
    }
}
