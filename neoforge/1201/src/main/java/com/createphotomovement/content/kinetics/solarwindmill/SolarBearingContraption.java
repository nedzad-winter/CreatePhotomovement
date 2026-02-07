package com.createphotomovement.content.kinetics.solarwindmill;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    protected int regularSailBlocks = 0; // Track separately since sailBlocks is zeroed
    protected boolean hasSkyAccess = false;

    // ContraptionType system doesn't exist in Create 1.20.1
    // Contraption types are identified by the class itself

    public SolarBearingContraption() {
        super();
    }

    public SolarBearingContraption(boolean isWindmill, Direction facing) {
        super(isWindmill, facing);
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        // Reset counters before assembly
        solarSailBlocks = 0;
        regularSailBlocks = 0;

        boolean result = super.assemble(world, pos);
        if (result) {
            // Check sky access for the bearing position at assembly time
            hasSkyAccess = checkSkyAccess(world, pos);
            LOGGER.info(
                    "[SolarBearingContraption] assemble FINISHED: SolarSails={}, TotalSails (super)={}, TotalBlocks={}",
                    solarSailBlocks, getSailBlocks(), getBlocks().size());
        }
        return result;
    }

    @Override
    public void addBlock(Level level, BlockPos pos, Pair<StructureBlockInfo, BlockEntity> capture) {
        // Create 1.20.1 uses 3-parameter addBlock signature with public visibility
        BlockPos localPos = pos.subtract(anchor);
        boolean isNew = !getBlocks().containsKey(localPos);

        // Check if this is a new block and track sail types separately
        if (isNew) {
            BlockState state = capture.getKey().state();
            if (state.getBlock() instanceof SolarSailBlock) {
                solarSailBlocks++;
                LOGGER.debug("[SolarBearingContraption] addBlock: Found SolarSail at {}, Count={}", localPos,
                        solarSailBlocks);
            } else if (isSail(state)) {
                regularSailBlocks++;
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
    public CompoundTag writeNBT(boolean spawnPacket) {
        // 1.20.1 signature: writeNBT(boolean spawnPacket)
        // CRITICAL: Zero sailBlocks BEFORE super.writeNBT() so parent class
        // serializes 0 to disk. This prevents double-counting on load.
        this.sailBlocks = 0;

        CompoundTag tag = super.writeNBT(spawnPacket);
        tag.putInt("SolarSails", solarSailBlocks);
        tag.putInt("RegularSails", regularSailBlocks); // Save our separately tracked count
        tag.putBoolean("HasSkyAccess", hasSkyAccess);
        if (!spawnPacket) {
            LOGGER.info(
                    "[SolarBearingContraption] writeNBT (DISK SAVE): solarSails={}, regularSails={}, hasSkyAccess={}, SUPER.Sails={}",
                    solarSailBlocks, regularSailBlocks, hasSkyAccess, getSailBlocks());
        }
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
        // Load block data first via super (populates 'blocks' map)
        super.readNBT(world, tag, spawnData);

        // Recalculate sail counts from actual blocks to ensure consistency
        // This fixes issues where NBT integers might desync or double-count on reload
        int recalcSolar = 0;
        int recalcTotal = 0;

        for (StructureBlockInfo info : getBlocks().values()) {
            BlockState state = info.state();
            boolean isSolar = state.getBlock() instanceof SolarSailBlock;

            // Solar Sails are definitely sails
            if (isSolar) {
                recalcSolar++;
                recalcTotal++;
            }
            // Check for regular sails (using Tag or instanceof SailBlock if needed)
            else if (isSail(state)) {
                recalcTotal++;
            }
        }

        // Fix the fields - track separately since sailBlocks will be zeroed
        this.solarSailBlocks = recalcSolar;
        this.regularSailBlocks = recalcTotal - recalcSolar; // Regular = total - solar
        // CRITICAL: Set sailBlocks to 0 to prevent parent class
        // (WindmillBearingBlockEntity)
        // from contributing its own capacity. SolarWindmillBearingBlockEntity handles
        // ALL
        // capacity calculation through its overridden calculateAddedStressCapacity().
        this.sailBlocks = 0;

        // Restore SkyAccess
        hasSkyAccess = tag.getBoolean("HasSkyAccess");

        LOGGER.info(
                "[SolarBearingContraption] readNBT: solarSails={}, regularSails={}, recalcTotal={}, sailBlocks SET TO 0, hasSkyAccess={}",
                solarSailBlocks, regularSailBlocks, recalcTotal, hasSkyAccess);
    }

    // Helper to mirror BearingContraption's sail check
    protected boolean isSail(BlockState state) {
        // Use tags if possible, or instanceof
        // We know SolarSailBlock is ours.
        if (state.getBlock() instanceof SolarSailBlock)
            return true;

        // For other Create sails
        if (state.is(com.simibubi.create.AllTags.AllBlockTags.WINDMILL_SAILS.tag))
            return true;

        return false;
    }

    /**
     * Returns the number of solar sail blocks in this contraption.
     */
    public int getSolarSailBlocks() {
        return solarSailBlocks;
    }

    /**
     * Sets the parent's sailBlocks field. Used by SolarWindmillBearingBlockEntity
     * to zero it out to prevent double capacity contribution.
     */
    public void setSailBlocks(int count) {
        this.sailBlocks = count;
    }

    /**
     * Returns the number of regular (non-solar) sail blocks in this contraption.
     * Tracked separately since sailBlocks is zeroed to prevent parent capacity
     * contribution.
     */
    public int getRegularSailBlocks() {
        return regularSailBlocks;
    }

    /**
     * Returns whether the bearing has proper sky access for solar power generation.
     * This is determined at assembly time and cached.
     */
    public boolean hasSkyAccess() {
        return hasSkyAccess;
    }
}
