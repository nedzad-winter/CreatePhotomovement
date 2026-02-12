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
        AllContraptionTypes.register();

        // Register config
        PMConfigs.register();

        // Register AttachedCheck for Solar Sails to ensure they connect to the bearing
        // and each other
        com.simibubi.create.api.contraption.BlockMovementChecks
                .registerAttachedCheck((state, world, pos, direction) -> {
                    if (state
                            .getBlock() instanceof com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock) {
                        // If axis is different from facing axis, it is attached (e.g. side attachment)
                        return com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.of(
                                direction.getAxis() != state.getValue(
                                        com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock.FACING)
                                        .getAxis());
                    }
                    return com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult.PASS;
                });

        LOGGER.info("Create Photomovement initialized!");
    }
}
