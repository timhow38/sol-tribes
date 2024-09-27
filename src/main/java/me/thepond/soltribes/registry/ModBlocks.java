package me.thepond.soltribes.registry;

import me.thepond.soltribes.block.TribeFlagBlock;
import me.thepond.soltribes.block.TribeTableBlock;
import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.block.TribeSideTableBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block TRIBE_TABLE = registerBlock("tribe_table",
            new TribeTableBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque().strength(50.0F, 1200.0F))
    );

    public static final Block SIDE_TRIBE_TABLE = registerBlock("side_tribe_table",
            new TribeSideTableBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque().strength(50.0F, 1200.0F))
    );

    public static final Block TRIBE_FLAG = registerBlockWithoutItem("tribe_flag",
            new TribeFlagBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque().strength(50.0F, 1200.0F))
    );

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(SOLTribes.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(SOLTribes.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SOLTribes.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings())
        );
    }

    public static void registerBlocks() {
        SOLTribes.LOGGER.info("Registering blocks for " + SOLTribes.MOD_ID);
    }

}
