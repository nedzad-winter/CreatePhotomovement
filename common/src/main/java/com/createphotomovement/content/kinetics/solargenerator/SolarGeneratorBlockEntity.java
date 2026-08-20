package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.infrastructure.config.PMConfigs;
import com.createphotomovement.logic.SolarGeneratorOutput;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    private boolean firstTick = true;

    public SolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Force network update on chunk load to sync with kinetic network
        if (level != null && !level.isClientSide) {
            firstTick = true;
            updateGeneratedRotation();
        }
    }

    /** Speed multiplier over the basic generator. Overridden by the advanced variant. */
    protected int speedMultiplier() {
        return SolarGeneratorOutput.BASIC_MULTIPLIER;
    }

    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;
        boolean raining = level != null && level.isRainingAt(worldPosition.above());
        return SolarGeneratorOutput.generatedSpeed(PMConfigs.server().generationSpeed.get(), speedMultiplier(),
                raining);
    }

    @Override
    public float calculateAddedStressCapacity() {
        return PMConfigs.server().stressCapacity.get();
    }

    protected boolean canGeneratePower() {
        if (level == null)
            return false;

        BlockPos abovePos = worldPosition.above();

        int skyLight = level.getBrightness(LightLayer.SKY, abovePos);
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < SolarGeneratorOutput.MIN_SKY_LIGHT)
            return false;

        // Snow and carpets let sky light through but still sit on the panel.
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

        // Light-transmitting blocks such as glass do NOT block generation; only a
        // genuinely blocked view of the sky does.
        return level.canSeeSky(abovePos);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide)
            return;

        // Right after placement or chunk load the kinetic network has not seen this
        // block yet, so push an update even when nothing appears to have changed.
        if (firstTick) {
            firstTick = false;
            updateGeneratedRotation();
            notifyUpdate();
            return;
        }

        // Compare against the target speed rather than merely "generating yes/no":
        // rain halves the speed without ever flipping canGeneratePower().
        float targetSpeed = getGeneratedSpeed();
        if (Math.abs(speed) != Math.abs(targetSpeed)
                || (targetSpeed != 0 && Math.signum(speed) != Math.signum(targetSpeed))) {
            updateGeneratedRotation();
        }
    }
}
