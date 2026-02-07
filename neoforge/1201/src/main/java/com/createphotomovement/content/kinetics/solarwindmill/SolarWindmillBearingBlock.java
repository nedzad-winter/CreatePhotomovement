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
        if (level.isClientSide())
            return null; // Kinetic blocks usually tick on server, or handled by super?
                         // Check WindmillBearingBlock if it has client ticker. Usually not.

        if (type == AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get()) {
            @SuppressWarnings("unchecked")
            BlockEntityTicker<T> ticker = (l, p, s, be) -> {
                if (be instanceof SolarWindmillBearingBlockEntity solarBe) {
                    solarBe.tick();
                    solarBe.solarTick();
                }
            };
            return ticker;
        }
        return null; // Or super.getTicker(level, state, type)?
                     // WindmillBearingBlock might have its own ticker.
                     // However, we are fully overriding behavior here.
                     // The WindmillBearingBlock only returns null?
                     // Usually KineticBlockEntity handles ticking via its own registration or logic.
                     // But here we explicitly call tick().
    }
}
