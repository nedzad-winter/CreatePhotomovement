package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    private boolean reversed = false;

    public SolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;
        return reversed ? -16f : 16f;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return 16f;
    }

    private boolean canGeneratePower() {
        if (level == null)
            return false;

        BlockPos abovePos = worldPosition.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // 1. Hard check for blocks that physically cover the sensor but might let light
        // through (like carpets)
        Block aboveBlock = aboveState.getBlock();
        if (aboveBlock instanceof SnowLayerBlock || aboveBlock instanceof CarpetBlock
                || aboveBlock == Blocks.MOSS_CARPET || aboveBlock == Blocks.SNOW)
            return false;

        // 2. Check if the block above blocks skylight
        if (!aboveState.propagatesSkylightDown(level, abovePos))
            return false;

        // 3. Ensure sufficient sky light reaches the sensor
        // This handles time of day (night/day) and weather automatically.
        // We must subtract 'skyDarken' because getBrightness(SKY) returns the internal
        // chunk light (0-15 static).
        int internalLight = level.getBrightness(LightLayer.SKY, abovePos);
        int effectiveLight = internalLight - level.getSkyDarken();

        // Threshold: 12 (User requested)
        if (effectiveLight < 12)
            return false;

        // 4. Ensure the generator is oriented correctly (Horizontal axis only)
        // If Axis is Y, the shafts are Up/Down, so the panel (side) is vertical -> No
        // power.
        if (getBlockState().getValue(RotatedPillarKineticBlock.AXIS) == Axis.Y)
            return false;

        return true;
    }

    private boolean previouslyGenerating = false;

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            boolean canGenerate = canGeneratePower();
            // Force update if generation state changes
            if (canGenerate != previouslyGenerating) {
                previouslyGenerating = canGenerate;
                updateGeneratedRotation();
            }
        }
    }

    public void toggleReversed() {
        reversed = !reversed;
        updateGeneratedRotation();
        notifyUpdate();
    }

    public boolean isReversed() {
        return reversed;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putBoolean("Reversed", reversed);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        reversed = compound.getBoolean("Reversed");
    }
}
