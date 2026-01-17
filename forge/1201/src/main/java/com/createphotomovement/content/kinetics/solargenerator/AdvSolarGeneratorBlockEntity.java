package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.infrastructure.config.PMConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AdvSolarGeneratorBlockEntity extends SolarGeneratorBlockEntity {

    public AdvSolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;

        float generatedSpeed = PMConfigs.server().generationSpeed.get() * 2;

        // Reduce speed by half during rain
        if (level != null && level.isRainingAt(worldPosition.above())) {
            generatedSpeed = generatedSpeed / 2;
        }
        return generatedSpeed;
    }
}
