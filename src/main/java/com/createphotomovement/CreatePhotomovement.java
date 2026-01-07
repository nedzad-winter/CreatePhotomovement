package com.createphotomovement;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreatePhotomovement.MOD_ID)
public class CreatePhotomovement {
    public static final String MOD_ID = "createphotomovement";

    public CreatePhotomovement(IEventBus modEventBus) {
        AllBlocks.BLOCKS.register(modEventBus);
        AllItems.ITEMS.register(modEventBus);
        AllCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        AllBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
