package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.infrastructure.config.PMConfigs;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HorizontalSolarGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    private float currentStressCapacity;
    private int updateTimer;
    private boolean firstTick = true;

    public HorizontalSolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.currentStressCapacity = PMConfigs.server().stressCapacity.get();
        this.updateTimer = 0;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Immediate update on placement/chunk load
        if (level != null && !level.isClientSide) {
            updateStressCapacity();
        }
    }

    /**
     * Called when the block is rotated (e.g., by wrench).
     * Forces an immediate recalculation of stress capacity.
     */
    public void forceUpdate() {
        if (level != null && !level.isClientSide) {
            updateTimer = 0; // Reset timer
            updateStressCapacity();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        // Check power generation conditions directly (works on both client and server)
        if (!canGeneratePower()) {
            return 0;
        }
        return PMConfigs.server().generationSpeed.get();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = currentStressCapacity;
        if (capacity < 0)
            capacity = 0;
        return capacity;
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide)
            return;

        // Immediate update on first tick (handles placement)
        if (firstTick) {
            firstTick = false;
            updateStressCapacity();
            // Force network update even if capacity didn't change
            updateGeneratedRotation();
            notifyUpdate();
            return;
        }

        // Check every 10 seconds (200 ticks)
        if (updateTimer++ >= 200) {
            updateTimer = 0;
            updateStressCapacity();
        }
    }

    private void updateStressCapacity() {
        // First check strict generation conditions (Light level > 12, no obstructions)
        if (!canGeneratePower()) {
            if (currentStressCapacity != 0) {
                currentStressCapacity = 0;
                updateGeneratedRotation();
                notifyUpdate();
            }
            return;
        }

        BlockState state = getBlockState();
        Direction facing = state.getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING);
        long time = level.getDayTime() % 24000;

        // Base config capacity
        float base = PMConfigs.server().stressCapacity.get();
        float peak = 4 * base;
        float min = 8;
        float newCapacity = min;

        // Clamp time to 0-12000 for curve calculation.
        long daylightTime = Math.min(time, 12000);
        float ratio = (float) daylightTime / 12000.0f; // 0.0 to 1.0
        ratio = Mth.clamp(ratio, 0f, 1f);

        if (facing == Direction.EAST) {
            // Starts high, goes low
            float factor = (1 - ratio) * (1 - ratio);
            newCapacity = min + (peak - min) * factor;
        } else if (facing == Direction.WEST) {
            // Starts low, goes high
            float factor = ratio * ratio;
            newCapacity = min + (peak - min) * factor;
        } else {
            // North/South - Default to min
            newCapacity = min;
        }

        // Round to nearest integer to avoid floating point numbers
        newCapacity = Math.round(newCapacity);

        if (Math.abs(newCapacity - currentStressCapacity) > 0.01f) {
            currentStressCapacity = newCapacity;
            updateGeneratedRotation();
            notifyUpdate();
        }
    }

    private boolean canGeneratePower() {
        if (level == null)
            return false;

        // Check if skylight can reach the block
        // Using light threshold of 12
        int skyLight = level.getBrightness(LightLayer.SKY, worldPosition.above());
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < 12) {
            return false;
        }

        BlockPos abovePos = worldPosition.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // Check for obstructions
        Block aboveBlock = aboveState.getBlock();
        if (aboveBlock instanceof SnowLayerBlock)
            return false;
        if (aboveBlock instanceof CarpetBlock)
            return false;
        if (aboveBlock == Blocks.MOSS_CARPET)
            return false;
        if (aboveBlock == Blocks.SNOW)
            return false; // Snow block

        // General opacity check
        // If it blocks light, it stops power.
        if (aboveState.getLightBlock(level, abovePos) > 0)
            return false;

        return true;
    }

    @Override
    protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries,
            boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("CurrentStressCapacity", currentStressCapacity);
    }

    @Override
    protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries,
            boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        currentStressCapacity = compound.getFloat("CurrentStressCapacity");
    }
}
