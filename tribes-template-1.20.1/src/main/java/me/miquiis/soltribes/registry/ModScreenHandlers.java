package me.thepond.soltribes.registry;

import me.thepond.soltribes.screenhandler.*;
import me.thepond.soltribes.SOLTribes;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static final ScreenHandlerType<TribeTableInformationScreenHandlerBlockEntity> TRIBE_TABLE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(SOLTribes.MOD_ID, "tribe_table_info"),
                    new ExtendedScreenHandlerType<>(TribeTableInformationScreenHandlerBlockEntity::new)
            );

    public static final ScreenHandlerType<TribeTableActivationScreenHandlerBlockEntity> TRIBE_TABLE_ACTIVATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(SOLTribes.MOD_ID, "tribe_table_activation"),
                    new ExtendedScreenHandlerType<>(TribeTableActivationScreenHandlerBlockEntity::new)
            );

    public static final ScreenHandlerType<TribeTableFlagScreenHandlerBlockEntity> TRIBE_TABLE_FLAG_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(SOLTribes.MOD_ID, "tribe_table_flag"),
                    new ExtendedScreenHandlerType<>(TribeTableFlagScreenHandlerBlockEntity::new)
            );

    public static final ScreenHandlerType<TribeTableEffectsScreenHandlerBlockEntity> TRIBE_TABLE_EFFECTS_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(SOLTribes.MOD_ID, "tribe_table_effects"),
                    new ExtendedScreenHandlerType<>(TribeTableEffectsScreenHandlerBlockEntity::new)
            );

    public static final ScreenHandlerType<TribeTableJoinScreenHandlerBlockEntity> TRIBE_TABLE_JOIN_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(SOLTribes.MOD_ID, "tribe_table_join"),
                    new ExtendedScreenHandlerType<>(TribeTableJoinScreenHandlerBlockEntity::new)
            );

    public static void registerScreenHandlers() {
        SOLTribes.LOGGER.info("Registering screen handlers for " + SOLTribes.MOD_ID);
    }
}
