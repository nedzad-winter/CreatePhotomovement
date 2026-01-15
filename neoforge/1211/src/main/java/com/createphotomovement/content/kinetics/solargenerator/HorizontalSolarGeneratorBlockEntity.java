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
        // Force recalculation and network update on chunk load
        if (level != null && !level.isClientSide) {
            // Reset firstTick to ensure we update on next tick
            firstTick = true;
            // Force immediate stress recalculation
            updateStressCapacity();
            // Always force network update on load to sync with kinetic network
            updateGeneratedRotation();
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
        float speed = PMConfigs.server().generationSpeed.get();
        // Reduce speed by half during rain
        if (level != null && level.isRainingAt(
                worldPosition.relative(getBlockState().getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING)))) {
            speed = speed / 2;
        }
        return speed;
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
        // First check strict generation conditions (Light level 12 or higher, no
        // obstructions)
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

        // Base config capacity
        float base = PMConfigs.server().stressCapacity.get();
        float min = 8;
        float newCapacity = min;

        // Check if the block is 2 to 10 blocks away we set the RPM to the minimum
        boolean distantObstruction = false;
        for (int i = 2; i <= 10; i++) {
            BlockPos checkPos = worldPosition.relative(facing, i);
            BlockState checkState = level.getBlockState(checkPos);
            // only for solid blocks
            if (checkState.getLightBlock(level, checkPos) > 0) {
                distantObstruction = true;
                break;
            }
        }

        if (distantObstruction) {
            newCapacity = min;
        } else {
            long time = level.getDayTime() % 24000;
            float peak = 4 * base;

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

        BlockState state = getBlockState();
        Direction facing = state.getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING);
        BlockPos frontPos = worldPosition.relative(facing);

        // Check if skylight can reach the block
        // Using light threshold of 12
        int skyLight = level.getBrightness(LightLayer.SKY, frontPos);
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < 12) {
            return false;
        }

        BlockState frontState = level.getBlockState(frontPos);

        // if the block right next to the solar face the generator does not produce
        // anything
        // only for solid blocks
        if (frontState.getLightBlock(level, frontPos) > 0) {
            return false;
        }

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
