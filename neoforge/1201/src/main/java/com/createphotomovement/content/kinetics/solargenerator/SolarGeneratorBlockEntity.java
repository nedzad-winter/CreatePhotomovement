package com.createphotomovement.content.kinetics.solargenerator;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.createphotomovement.infrastructure.config.PMConfigs;

public class SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    public SolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Force network update on chunk load to sync with kinetic network
        if (level != null && !level.isClientSide) {
            updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;
        float speed = PMConfigs.server().generationSpeed.get();
        // Reduce speed by half during rain
        if (level != null && level.isRainingAt(worldPosition.above())) {
            speed = speed / 2;
        }
        return speed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return PMConfigs.server().stressCapacity.get();
    }

    protected boolean canGeneratePower() {
        if (level == null)
            return false;

        // Strict sky visibility check: The block directly above must be able to see the
        // sky
        if (!level.canSeeSky(worldPosition.above()))
            return false;

        int skyLight = level.getBrightness(LightLayer.SKY, worldPosition.above());
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < 12) {
            return false;
        }

        BlockPos abovePos = worldPosition.above();
        BlockState aboveState = level.getBlockState(abovePos);

        Block aboveBlock = aboveState.getBlock();
        if (aboveBlock instanceof SnowLayerBlock)
            return false;
        if (aboveBlock instanceof CarpetBlock)
            return false;
        if (aboveBlock == Blocks.MOSS_CARPET)
            return false;
        if (aboveBlock == Blocks.SNOW)
            return false;

        if (aboveState.getLightBlock(level, abovePos) > 0)
            return false;

        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide) {
            float targetSpeed = getGeneratedSpeed();

            if (Math.abs(speed) != Math.abs(targetSpeed)
                    || (targetSpeed != 0 && Math.signum(speed) != Math.signum(targetSpeed))) {
                updateGeneratedRotation();
            }
        }
    }
}
