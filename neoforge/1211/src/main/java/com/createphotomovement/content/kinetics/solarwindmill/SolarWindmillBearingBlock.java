package com.createphotomovement.content.kinetics.solarwindmill;

import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Solar Windmill Bearing block - generates double RPM from Solar Sails
 * when proper solar conditions are met.
 */
public class SolarWindmillBearingBlock extends WindmillBearingBlock {

    public SolarWindmillBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends WindmillBearingBlockEntity> getBlockEntityType() {
        return com.createphotomovement.AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get();
    }
}
