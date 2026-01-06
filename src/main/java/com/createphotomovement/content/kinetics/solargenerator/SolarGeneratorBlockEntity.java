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
        if (level == null || level.isClientSide)
            return false;

        // Check if it's daytime
        if (!level.isDay())
            return false;

        // Check if skylight can reach the block
        if (level.getBrightness(LightLayer.SKY, worldPosition.above()) < 15) {
             // We can double check strict sky visibility if needed, but brightness is a good proxy.
             // However, Create typically uses level.canSeeSky(pos.above())
             if (!level.canSeeSky(worldPosition.above()))
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
        if (aboveBlock instanceof SnowLayerBlock) return false;
        if (aboveBlock instanceof CarpetBlock) return false;
        if (aboveBlock == Blocks.MOSS_CARPET) return false;
        if (aboveBlock == Blocks.SNOW) return false; // Snow block

        // General opacity check
        // If it blocks light, it stops power.
        // isSolidRender is a decent proxy for "full block", but opacity is better for light mechanics.
        if (aboveState.getLightBlock(level, abovePos) > 0)
            return false;

        return true;
    }
    
    @Override
    public void tick() {
        super.tick();
        // Periodically check for condition updates if purely environmental
        // But GeneratingKineticBlockEntity normally doesn't auto-update speed unless notified.
        // We should check every tick or every few ticks if conditions changed.
        
        if (!level.isClientSide) {
             boolean shouldBeRunning = canGeneratePower();
             float targetSpeed = shouldBeRunning ? (reversed ? -16f : 16f) : 0f;
             
             if (Math.abs(getGeneratedSpeed()) != Math.abs(targetSpeed) || (targetSpeed != 0 && speed == 0)) {
                 updateGeneratedRotation();
             } else if (targetSpeed != 0 && Math.signum(getGeneratedSpeed()) != Math.signum(targetSpeed)) {
                 updateGeneratedRotation();
             }
        }
    }

    public void toggleReversed() {
        reversed = !reversed;
        updateGeneratedRotation();
        notifyUpdate();
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
