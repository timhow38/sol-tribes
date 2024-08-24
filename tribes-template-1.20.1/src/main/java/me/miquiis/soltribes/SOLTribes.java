package me.thepond.soltribes;

import me.thepond.solregions.events.RegionEvent;
import me.thepond.soltribes.block.TribeSideTableBlock;
import me.thepond.soltribes.block.entity.TribeFlagBlockEntity;
import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.command.ModCommands;
import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.packets.VisitType;
import me.thepond.soltribes.registry.*;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SOLTribes implements ModInitializer {

	public static final String MOD_ID = "soltribes";
    public static final Logger LOGGER = LoggerFactory.getLogger("soltribes");

	@Override
	public void onInitialize() {
		LOGGER.info("SOL - Tribes initialized");
		CommandRegistrationCallback.EVENT.register(new ModCommands());
		ModPackets.registerC2SPackets();
		ModBlocks.registerBlocks();
		ModItems.registerItems();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClient) {
				if (world.getBlockEntity(pos) instanceof TribeFlagBlockEntity tribeFlagBlockEntity) {
					if (tribeFlagBlockEntity.getTribe() != null) {
						TribesData tribeData = TribesDataManager.getTribesData((ServerWorld) world);
						Tribe flagTribe = tribeData.getTribe(tribeFlagBlockEntity.getTribe().getTribeId());
						Tribe playerTribe = tribeData.getTribeFromPlayer(player.getUuid());
						if (player.isCreative() || (playerTribe != null && playerTribe.getTribeId().equals(tribeFlagBlockEntity.getTribe().getTribeId()))) {
							tribeFlagBlockEntity.onRemove(world);
							flagTribe.removeTribeSettlement(tribeFlagBlockEntity.getTribeSettlementId());
							flagTribe.sendTableUpdate(world);
							tribeData.setDirty(true);
							for (ServerPlayerEntity serverPlayerEntity : world.getServer().getPlayerManager().getPlayerList()) {
								if (serverPlayerEntity.getUuid().equals(player.getUuid())) {
									PacketByteBuf buf = PacketByteBufs.create();
									buf.writeEnumConstant(VisitType.LEAVE);
									buf.writeUuid(flagTribe.getTribeId());
									ServerPlayNetworking.send(serverPlayerEntity, ModPackets.REGION_VISIT, buf);
								}
							}
							return true;
						} else {
							return false;
						}
					}
				} else if (world.getBlockEntity(pos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
					if (tribeTableBlockEntity.getTribe() != null) {
						return canBreakTribeTable(world, player, tribeTableBlockEntity);
					}
				} else if (world.getBlockState(pos).getBlock() == ModBlocks.SIDE_TRIBE_TABLE) {
					BlockPos mainTablePos = pos.offset(world.getBlockState(pos).get(TribeSideTableBlock.DIRECTION).getOpposite());
					if (world.getBlockEntity(mainTablePos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
						if (tribeTableBlockEntity.getTribe() != null) {
							return canBreakTribeTable(world, player, tribeTableBlockEntity);
						}
					}
				}
			}
			return true;
		});

		RegionEvent.ENTER_REGION.register((minecraftServer, serverPlayerEntity, region) -> {
			TribesData tribeData = TribesDataManager.getTribesData((ServerWorld) serverPlayerEntity.getWorld());
			List<Tribe> tribesInRegion = new ArrayList<>();
			for (Tribe tribe : tribeData.getTribes()) {
				for (TribeSettlement tribeSettlement : tribe.getTribeSettlements()) {
					if (region.isInRegion(tribeSettlement.getTribeSettlementPosition())) {
						tribesInRegion.add(tribe);
					}
				}
			}
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeEnumConstant(VisitType.ENTER);
			buf.writeCollection(tribesInRegion, (buf1, tribe) -> {
				buf1.writeNbt(tribe.toNbt());
			});
			ServerPlayNetworking.send(serverPlayerEntity, ModPackets.REGION_VISIT, buf);
		});

		RegionEvent.LEAVE_REGION.register((minecraftServer, serverPlayerEntity, region) -> {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeEnumConstant(VisitType.LEAVE);
			ServerPlayNetworking.send(serverPlayerEntity, ModPackets.REGION_VISIT, buf);
		});
	}

	private boolean canBreakTribeTable(World world, PlayerEntity player, TribeTableBlockEntity tribeTableBlockEntity) {
		TribesData tribeData = TribesDataManager.getTribesData((ServerWorld) world);
		Tribe playerTribe = tribeData.getTribeFromPlayer(player.getUuid());
		return player.isCreative() || !tribeTableBlockEntity.isActivated() || (playerTribe != null && playerTribe.getTribeId().equals(tribeTableBlockEntity.getTribe().getTribeId()));
	}
}