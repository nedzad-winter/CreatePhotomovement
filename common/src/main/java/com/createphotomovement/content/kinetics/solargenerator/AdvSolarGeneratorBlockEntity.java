package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.logic.SolarGeneratorOutput;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AdvSolarGeneratorBlockEntity extends SolarGeneratorBlockEntity {

    public AdvSolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected int speedMultiplier() {
        return SolarGeneratorOutput.ADVANCED_MULTIPLIER;
    }
}
