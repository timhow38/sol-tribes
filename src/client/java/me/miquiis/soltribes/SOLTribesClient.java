package me.thepond.soltribes;

import me.thepond.solregions.events.RenderRegionEvent;
import me.thepond.soltribes.registry.ModBlockEntities;
import me.thepond.soltribes.registry.ModBlocks;
import me.thepond.soltribes.registry.ModScreenHandlers;
import me.thepond.soltribes.renderer.TribeBannerRenderer;
import me.thepond.soltribes.renderer.TribeFlagBlockEntityRenderer;
import me.thepond.soltribes.screen.*;
import me.thepond.soltribes.tribe.Tribe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class SOLTribesClient implements ClientModInitializer {

	private static Tribe currentTribe = null;

	private static float boostedReach = -1F;

	public static float getBoostedReach() {
		return boostedReach;
	}

	public static void setBoostedReach(float boostedReach) {
		SOLTribesClient.boostedReach = boostedReach;
	}

	@Override
	public void onInitializeClient() {
		SOLTribesClientConfig.loadOptions();
		SOLTribesServerConfig.loadOptions();
		ClientModPackets.registerS2CPackets();
		HandledScreens.register(ModScreenHandlers.TRIBE_TABLE_SCREEN_HANDLER, TribeTableInformationScreen::new);
		HandledScreens.register(ModScreenHandlers.TRIBE_TABLE_ACTIVATION_SCREEN_HANDLER, TribeTableActivationScreen::new);
		HandledScreens.register(ModScreenHandlers.TRIBE_TABLE_FLAG_SCREEN_HANDLER, TribeTableFlagScreen::new);
		HandledScreens.register(ModScreenHandlers.TRIBE_TABLE_EFFECTS_SCREEN_HANDLER, TribeTableEffectsScreen::new);
		HandledScreens.register(ModScreenHandlers.TRIBE_TABLE_JOIN_SCREEN_HANDLER, TribeTableJoinScreen::new);
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRIBE_TABLE, RenderLayer.getCutout());
		net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(ModBlockEntities.TRIBE_FLAG_BLOCK_ENTITY, TribeFlagBlockEntityRenderer::new);
		RenderRegionEvent.TEXT.register(new TribeBannerRenderer());
	}

	public static Tribe getCurrentTribe() {
		return currentTribe;
	}

	public static void setCurrentTribe(Tribe tribe) {
		currentTribe = tribe;
	}
}