package me.thepond.soltribes.registry;

import me.thepond.soltribes.item.TribeFlagItem;
import me.thepond.soltribes.SOLTribes;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item TRIBE_FLAG = Registry.register(Registries.ITEM, new Identifier(SOLTribes.MOD_ID, "tribe_flag"), new TribeFlagItem(new FabricItemSettings()));

    public static void registerItems() {
        SOLTribes.LOGGER.debug("Registering items...");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(TRIBE_FLAG);
            entries.add(ModBlocks.TRIBE_TABLE);
        });
    }
}
