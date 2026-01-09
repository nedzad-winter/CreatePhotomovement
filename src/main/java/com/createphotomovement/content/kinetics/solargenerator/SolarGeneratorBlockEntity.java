package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.createphotomovement.infrastructure.config.PMConfigs;

public class SolarGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    private boolean reversed = false;

    public SolarGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        if (!canGeneratePower())
            return 0;
        int speed = PMConfigs.server().generationSpeed.get();
        return reversed ? -speed : speed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return PMConfigs.server().stressCapacity.get();
    }

    private boolean canGeneratePower() {
        if (level == null)
            return false;

        // Check if skylight can reach the block
        // Using light threshold of 12 instead of hard isDay() check
        int skyLight = level.getBrightness(LightLayer.SKY, worldPosition.above());
        int currentSkyLight = skyLight - level.getSkyDarken();
        if (currentSkyLight < 12) {
            return false;
        }

        BlockPos abovePos = worldPosition.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // Check for obstructions
        // Full solid blocks block sunlight typically, but we want specific behavior
        // The user specified:
        // - Full block stops rotation
        // - Transparent blocks don't stop
        // - Snow, moss, carpets DO stop it

        // Hard checks for specific blocking items
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
        // isSolidRender is a decent proxy for "full block", but opacity is better for
        // light mechanics.
        if (aboveState.getLightBlock(level, abovePos) > 0)
            return false;

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        // Periodically check for condition updates if purely environmental
        // But GeneratingKineticBlockEntity normally doesn't auto-update speed unless
        // notified.
        // We should check every tick or every few ticks if conditions changed.

        if (!level.isClientSide) {
            float targetSpeed = getGeneratedSpeed();

            if (Math.abs(speed) != Math.abs(targetSpeed)
                    || (targetSpeed != 0 && Math.signum(speed) != Math.signum(targetSpeed))) {
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
