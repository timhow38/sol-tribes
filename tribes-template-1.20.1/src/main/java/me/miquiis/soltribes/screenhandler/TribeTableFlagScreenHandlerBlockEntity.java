package me.thepond.soltribes.screenhandler;

import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.registry.ModScreenHandlers;
import me.thepond.soltribes.screenhandler.slot.FlagSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class TribeTableFlagScreenHandlerBlockEntity extends ScreenHandler implements ITribeTableBlockEntity {

    private final Inventory inventory;
    public final TribeTableBlockEntity blockEntity;

    public TribeTableFlagScreenHandlerBlockEntity(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public TribeTableFlagScreenHandlerBlockEntity(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.TRIBE_TABLE_FLAG_SCREEN_HANDLER, syncId);
        this.blockEntity = (TribeTableBlockEntity) blockEntity;

        checkSize(this.blockEntity.getFlagsPageInventory(), 1);
        this.inventory = this.blockEntity.getFlagsPageInventory();
        inventory.onOpen(playerInventory.player);

        this.addSlot(new FlagSlot(inventory, 0, 80, 81));

        this.addPlayerHotbar(playerInventory);
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return stack.isIn(ItemTags.BANNERS);
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public TribeTableBlockEntity getTribeTableBlockEntity() {
        return this.blockEntity;
    }
}
