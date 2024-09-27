package me.thepond.soltribes.registry;

import me.thepond.soltribes.block.entity.TribeFlagBlockEntity;
import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<TribeTableBlockEntity> TRIBE_TABLE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SOLTribes.MOD_ID, "tribe_table_be"),
                    FabricBlockEntityTypeBuilder.create(TribeTableBlockEntity::new, ModBlocks.TRIBE_TABLE).build()
            );

    public static final BlockEntityType<TribeFlagBlockEntity> TRIBE_FLAG_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SOLTribes.MOD_ID, "tribe_flag_be"),
                    FabricBlockEntityTypeBuilder.create(TribeFlagBlockEntity::new, ModBlocks.TRIBE_FLAG).build()
            );

    public static void registerBlockEntities() {
        SOLTribes.LOGGER.info("Registering block entities for " + SOLTribes.MOD_ID);
    }
}
