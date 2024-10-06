package me.thepond.soltribes.screenhandler;

import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.registry.ModScreenHandlers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

public class TribeTableActivationScreenHandlerBlockEntity extends ScreenHandler implements ITribeTableBlockEntity {

    public final TribeTableBlockEntity blockEntity;

    public TribeTableActivationScreenHandlerBlockEntity(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public TribeTableActivationScreenHandlerBlockEntity(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.TRIBE_TABLE_ACTIVATION_SCREEN_HANDLER, syncId);
        this.blockEntity = (TribeTableBlockEntity) blockEntity;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        return super.onButtonClick(player, id);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public TribeTableBlockEntity getTribeTableBlockEntity() {
        return this.blockEntity;
    }
}
