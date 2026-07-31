package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.infrastructure.config.PMConfigs;
import com.createphotomovement.logic.HorizontalSolarOutput;
import com.createphotomovement.logic.SolarGeneratorOutput;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LightLayer;
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
        boolean raining = level != null && level.isRainingAt(
                worldPosition.relative(getBlockState().getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING)));
        return SolarGeneratorOutput.generatedSpeed(PMConfigs.server().generationSpeed.get(), speedMultiplier(),
                raining);
    }

    /** Speed multiplier over the basic generator. Overridden by the advanced variant. */
    protected int speedMultiplier() {
        return SolarGeneratorOutput.BASIC_MULTIPLIER;
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

        // Per-tick poll: react instantly when canGeneratePower() flips
        // (front-face obstruction). Otherwise the 200-tick cycle below would
        // delay shaft start/stop by up to 10 seconds.
        float targetSpeed = getGeneratedSpeed();
        if (Math.abs(speed) != Math.abs(targetSpeed)) {
            updateGeneratedRotation();
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

        float newCapacity = HorizontalSolarOutput.stressCapacity(SolarFacings.of(facing), level.getDayTime(),
                PMConfigs.server().stressCapacity.get(), hasDistantObstruction(facing));

        if (Math.abs(newCapacity - currentStressCapacity) > 0.01f) {
            currentStressCapacity = newCapacity;
            updateGeneratedRotation();
            notifyUpdate();
        }
    }

    /**
     * Whether a solid block stands in the panel's line of sight, between
     * {@link HorizontalSolarOutput#OBSTRUCTION_SCAN_FROM} and
     * {@link HorizontalSolarOutput#OBSTRUCTION_SCAN_TO} blocks away. The block
     * directly in front is handled by {@link #canGeneratePower()} instead.
     */
    private boolean hasDistantObstruction(Direction facing) {
        for (int i = HorizontalSolarOutput.OBSTRUCTION_SCAN_FROM; i <= HorizontalSolarOutput.OBSTRUCTION_SCAN_TO; i++) {
            BlockPos checkPos = worldPosition.relative(facing, i);
            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.getLightBlock(level, checkPos) > 0)
                return true;
        }
        return false;
    }

    protected boolean canGeneratePower() {
        if (level == null)
            return false;

        BlockState state = getBlockState();
        Direction facing = state.getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING);
        BlockPos frontPos = worldPosition.relative(facing);

        // Check if skylight can reach the block
        int skyLight = level.getBrightness(LightLayer.SKY, frontPos);
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < SolarGeneratorOutput.MIN_SKY_LIGHT) {
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
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("CurrentStressCapacity", currentStressCapacity);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        currentStressCapacity = compound.getFloat("CurrentStressCapacity");
    }
}
