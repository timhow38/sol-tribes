package me.thepond.soltribes.packets;

import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class OpenTabTribeTableC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ServerPlayerEntity player = handler.player;
        BlockPos pos = buf.readBlockPos();
        int tab = buf.readInt();
        server.execute(() -> {
            if (player.getWorld().getBlockEntity(pos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                        tribeTableBlockEntity.writeScreenOpeningData(player, buf);
                    }

                    @Override
                    public Text getDisplayName() {
                        return tribeTableBlockEntity.getDisplayName();
                    }

                    @Nullable
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        return tribeTableBlockEntity.openScreenTab(tab, syncId, playerInventory, player);
                    }
                });
            }
        });
    }

}
