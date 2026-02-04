package com.createphotomovement.content.kinetics.solarwindmill;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/**
 * Solar Bearing Contraption - extends BearingContraption to track solar sails
 * separately
 * and check sky access at assembly time.
 */
public class SolarBearingContraption extends BearingContraption {

    private static final Logger LOGGER = LogUtils.getLogger();

    protected int solarSailBlocks = 0;
    protected boolean hasSkyAccess = false;

    @Override
    public ContraptionType getType() {
        return com.createphotomovement.AllContraptionTypes.SOLAR_BEARING.get();
    }

    public SolarBearingContraption() {
        super();
    }

    public SolarBearingContraption(boolean isWindmill, Direction facing) {
        super(isWindmill, facing);
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        boolean result = super.assemble(world, pos);
        if (result) {
            // Check sky access for the bearing position at assembly time
            hasSkyAccess = checkSkyAccess(world, pos);
        }
        return result;
    }

    @Override
    public void addBlock(Level level, BlockPos pos, Pair<StructureBlockInfo, BlockEntity> capture) {
        BlockPos localPos = pos.subtract(anchor);

        // Check if this is a new block and if it's a solar sail
        if (!getBlocks().containsKey(localPos)) {
            BlockState state = capture.getKey().state();
            if (state.getBlock() instanceof SolarSailBlock) {
                solarSailBlocks++;
            }
        }

        super.addBlock(level, pos, capture);
    }

    /**
     * Checks if the bearing position has proper sky access for solar power.
     * Scans a 5x5 area around the bearing - returns true if ANY position can see
     * sky.
     * This tolerates shaft/support blocks while still detecting open sky nearby.
     */
    private boolean checkSkyAccess(Level world, BlockPos pos) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (world.canSeeSky(pos.offset(x, 1, z))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        tag.putInt("SolarSails", solarSailBlocks);
        tag.putBoolean("HasSkyAccess", hasSkyAccess);
        LOGGER.info("[SolarBearingContraption] writeNBT: solarSails={}, hasSkyAccess={}, spawnPacket={}",
                solarSailBlocks, hasSkyAccess, spawnPacket);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
        // Load values from NBT - addBlock() is NOT called during deserialization
        // (Contraption.readBlocksCompound directly populates the blocks map)
        int nbtValue = tag.getInt("SolarSails");
        hasSkyAccess = tag.getBoolean("HasSkyAccess");
        LOGGER.info(
                "[SolarBearingContraption] readNBT: current={}, nbtValue={}, hasSkyAccess={}, spawnData={}",
                solarSailBlocks, nbtValue, hasSkyAccess, spawnData);
        solarSailBlocks = nbtValue;
        super.readNBT(world, tag, spawnData);
        LOGGER.info("[SolarBearingContraption] readNBT AFTER super: solarSails={}", solarSailBlocks);
    }

    /**
     * Returns the number of solar sail blocks in this contraption.
     */
    public int getSolarSailBlocks() {
        return solarSailBlocks;
    }

    /**
     * Returns the number of regular (non-solar) sail blocks in this contraption.
     * This is calculated as total sail blocks minus solar sail blocks.
     */
    public int getRegularSailBlocks() {
        return getSailBlocks() - solarSailBlocks;
    }

    /**
     * Returns whether the bearing has proper sky access for solar power generation.
     * This is determined at assembly time and cached.
     */
    public boolean hasSkyAccess() {
        return hasSkyAccess;
    }
}
