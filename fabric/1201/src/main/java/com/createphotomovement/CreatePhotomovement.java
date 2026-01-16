package com.createphotomovement;

import com.createphotomovement.infrastructure.config.PMConfigs;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class CreatePhotomovement implements ModInitializer {
    public static final String MOD_ID = "createphotomovement";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        // Register blocks first (items depend on blocks)
        AllBlocks.register();
        AllItems.register();
        AllBlockEntityTypes.register();
        AllCreativeTabs.register();

        // Register config
        PMConfigs.register();

        LOGGER.info("Create Photomovement initialized!");
    }
}
