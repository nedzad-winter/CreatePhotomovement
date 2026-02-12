package com.createphotomovement.content.kinetics.solarwindmill;

import com.createphotomovement.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Solar Windmill Bearing block - generates double RPM from Solar Sails
 * when proper solar conditions are met.
 */
public class SolarWindmillBearingBlock extends WindmillBearingBlock {

    public SolarWindmillBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends WindmillBearingBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return (l, p, s, be) -> {
            if (be instanceof SolarWindmillBearingBlockEntity solarBe) {
                // Manually call the standard tick logic (inherited from
                // WindmillBearingBlockEntity -> SmartBlockEntity)
                solarBe.tick();
                // Then call our custom solar logic (server-side only)
                if (!level.isClientSide) {
                    solarBe.solarTick();
                }
            }
        };
    }
}
