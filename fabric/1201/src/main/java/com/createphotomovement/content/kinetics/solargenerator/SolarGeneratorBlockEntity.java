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

    private boolean wasGenerating = false;
    private boolean firstTick = true;

    public SolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Force network update on chunk load to sync with kinetic network
        if (level != null && !level.isClientSide) {
            wasGenerating = canGeneratePower();
            updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;
        float generatedSpeed = PMConfigs.server().generationSpeed.get();
        // Reduce speed by half during rain
        if (level != null && level.isRainingAt(worldPosition.above())) {
            generatedSpeed = generatedSpeed / 2;
        }
        return generatedSpeed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return PMConfigs.server().stressCapacity.get();
    }

    protected boolean canGeneratePower() {
        if (level == null)
            return false;

        BlockPos abovePos = worldPosition.above();

        // Check sky light level (must be 12 or higher)
        int skyLight = level.getBrightness(LightLayer.SKY, abovePos);
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < 12) {
            return false;
        }

        // Check block immediately above for snow/carpets (special cases that block even
        // with sky access)
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

        // Check for direct sky access - no solid blocks anywhere above
        // canSeeSky returns true only if there's a clear path to the sky
        if (!level.canSeeSky(abovePos)) {
            return false;
        }

        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide)
            return;

        // Force update on first tick (handles initial placement)
        if (firstTick) {
            firstTick = false;
            wasGenerating = canGeneratePower();
            updateGeneratedRotation();
            notifyUpdate();
            return;
        }

        boolean canGenerate = canGeneratePower();

        // Update if generation state changed
        if (canGenerate != wasGenerating) {
            wasGenerating = canGenerate;
            updateGeneratedRotation();
            notifyUpdate();
        }
    }
}
