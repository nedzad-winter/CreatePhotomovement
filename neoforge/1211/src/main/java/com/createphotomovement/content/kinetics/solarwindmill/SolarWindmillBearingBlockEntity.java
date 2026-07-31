package com.createphotomovement.content.kinetics.solarwindmill;

/*
 * Note: contains the fix for the SU doubling bug (2026-02-04). The kinetic network
 * counted this bearing twice after a world reload -- once via unloadedCapacity from
 * initFromTE() and once as a live source. See initialize() and onNetworkChange() below.
 */
import java.util.List;

import com.createphotomovement.logic.Weather;
import com.createphotomovement.logic.WindmillOutput;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Solar Windmill Bearing Block Entity - calculates RPM and SU based on solar
 * conditions.
 * 
 * Solar Sails provide 2x SU (Stress Units) contribution when:
 * - It's daytime
 * - Weather is clear
 * - Bearing has sky access (checked at assembly time)
 * 
 * RPM is calculated normally (same as regular windmill bearing).
 * The solar bonus applies to stress capacity (SU), not speed.
 * 
 * During rain: Solar Sails provide 1.5x SU
 * During thunder/night/no sky access: Solar Sails provide 1x SU (no bonus)
 */
public class SolarWindmillBearingBlockEntity extends WindmillBearingBlockEntity {

    // Cached at assembly time
    private int regularSailCount = 0;
    private int solarSailCount = 0;
    private boolean hasSkyAccess = false;
    private float lastSolarMultiplier = -1;
    private int warmup = 10; // Delay before refreshing from contraption

    public SolarWindmillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void solarTick() {
        if (level == null || level.isClientSide)
            return;

        // Handle warmup after world load - refresh from contraption
        if (warmup > 0) {
            warmup--;
            if (warmup == 0) {
                // Force refresh from contraption after load delay
                updateGeneratedRotation();
            }
            return;
        }

        float currentMultiplier = getSolarMultiplier();
        if (Math.abs(currentMultiplier - lastSolarMultiplier) > 0.001f) {
            lastSolarMultiplier = currentMultiplier;
            updateGeneratedRotation();
        }
    }

    /**
     * Override assemble to use SolarBearingContraption which tracks solar sails
     * and checks sky access at assembly time.
     */
    @Override
    public void assemble() {
        if (!(level.getBlockState(worldPosition).getBlock() instanceof BearingBlock))
            return;

        Direction direction = getBlockState().getValue(BearingBlock.FACING);

        // Use our custom SolarBearingContraption instead of regular BearingContraption
        SolarBearingContraption contraption = new SolarBearingContraption(true, direction);
        try {
            if (!contraption.assemble(level, worldPosition))
                return;

            lastException = null;
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return;
        }

        // Update our cached sail counts from the contraption
        this.solarSailCount = contraption.getSolarSailBlocks();
        this.regularSailCount = contraption.getRegularSailBlocks();
        this.hasSkyAccess = contraption.hasSkyAccess();

        award(AllAdvancements.WINDMILL);
        if (contraption.getSailBlocks() >= 16 * 8)
            award(AllAdvancements.WINDMILL_MAXED);

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        movedContraption = ControlledContraptionEntity.create(level, this, contraption);
        BlockPos anchor = worldPosition.relative(direction);
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        movedContraption.setRotationAxis(direction.getAxis());
        level.addFreshEntity(movedContraption);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

        if (contraption.containsBlockBreakers())
            award(AllAdvancements.CONTRAPTION_ACTORS);

        running = true;
        angle = 0;
        sendData();
        updateGeneratedRotation();
    }

    @Override
    public void updateGeneratedRotation() {

        // Update sail counts when contraption is assembled/updated
        if (movedContraption != null) {
            Contraption c = movedContraption.getContraption();

            if (c instanceof SolarBearingContraption sbc) {
                this.solarSailCount = sbc.getSolarSailBlocks();
                this.regularSailCount = sbc.getRegularSailBlocks();
                this.hasSkyAccess = sbc.hasSkyAccess();

                // CRITICAL: Zero out parent's sailBlocks to prevent double-counting!
                // We handle all sail capacity ourselves in calculateAddedStressCapacity().
                sbc.setSailBlocks(0);
            } else if (c instanceof BearingContraption bc) {
                // Fallback for regular BearingContraption
                this.regularSailCount = bc.getSailBlocks();
                this.solarSailCount = 0;
                this.hasSkyAccess = false;

                // Note: Can't zero sailBlocks for regular BearingContraption - parent handles
                // this
            }
        } else {
            // Detached or invalid contraption - reset counts to prevent ghost SU
            this.regularSailCount = 0;
            this.solarSailCount = 0;
            this.hasSkyAccess = false;

        }
        super.updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (!running) {
            return 0;
        }
        if (movedContraption == null) {
            return 0; // Prevent ghost speed from stale lastGeneratedSpeed
        }

        // RPM is calculated normally - no solar bonus on speed
        return WindmillOutput.generatedSpeed(regularSailCount, solarSailCount,
                AllConfigs.server().kinetics.windmillSailsPerRPM.get(), getAngleSpeedDirection());
    }

    /**
     * Gets the solar multiplier for SU calculation.
     * - 2.0 = Full solar bonus (clear day with sky access)
     * - 1.5 = Rain bonus (rainy day with sky access)
     * - 1.0 = No bonus (night, thunder, no sky access)
     */
    private float getSolarMultiplier() {
        if (level == null)
            return WindmillOutput.MULTIPLIER_NONE;
        return WindmillOutput.solarMultiplier(hasSkyAccess, level.getDayTime(),
                Weather.of(level.isRaining(), level.isThundering()));
    }

    @Override
    public float calculateAddedStressCapacity() {

        // Guard against detached/invalid bearings reporting capacity
        if (!running || movedContraption == null)
            return 0;

        float result = WindmillOutput.stressCapacityPerRpm(regularSailCount, solarSailCount,
                AllConfigs.server().kinetics.windmillSailsPerRPM.get(), getSolarMultiplier());

        this.lastCapacityProvided = result;
        return result;
    }

    /**
     * Called during contraption assembly to set sail counts and sky access.
     */
    public void setSailCounts(int regular, int solar, boolean skyAccess) {
        this.regularSailCount = regular;
        this.solarSailCount = solar;
        this.hasSkyAccess = skyAccess;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (clientPacket) {
            compound.putInt("RegularSails", regularSailCount);
            compound.putInt("SolarSails", solarSailCount);
            compound.putBoolean("HasSkyAccess", hasSkyAccess);
        } else {
            // CRITICAL: Zero BOTH 'capacity' AND 'lastCapacityProvided' BEFORE
            // super.write()!
            this.capacity = 0;
            this.lastCapacityProvided = 0;
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        if (clientPacket) {
            regularSailCount = compound.getInt("RegularSails");
            solarSailCount = compound.getInt("SolarSails");
            hasSkyAccess = compound.getBoolean("HasSkyAccess");
        } else {
            // Server: reset to prevent ghost SU
            regularSailCount = 0;
            solarSailCount = 0;
            hasSkyAccess = false;
            // Force capacity reset - but super.read() already loaded from NBT
            this.lastCapacityProvided = 0;
            this.capacity = 0;
        }
    }

    @Override
    public void initialize() {

        // CRITICAL FIX: Force the network to recalculate from scratch.
        // The problem is that initFromTE() sets unloadedCapacity from stale NBT data,
        // and our BE is then double-counted (once in unloadedCapacity, once in
        // sources).
        //
        // We mark the network as uninitialized BEFORE super.initialize() runs.
        // This way, initFromTE() will be called with OUR (zeroed) capacity value,
        // not whatever was cached in the network from a previous session.
        if (hasNetwork() && !level.isClientSide) {
            KineticNetwork net = getOrCreateNetwork();
            if (net.initialized) {
                // Force re-initialization with our zeroed values
                net.initialized = false;

            }
        }

        super.initialize();
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        // CRITICAL FIX: The KineticNetwork is calculating double capacity because it
        // includes both unloadedCapacity (from initFromTE) AND our contribution via
        // sources.
        // We override this to recalculate the correct capacity ourselves.

        // Calculate what OUR contribution should be
        float ourCapacity = calculateAddedStressCapacity(); // per RPM
        float ourSpeed = Math.abs(getGeneratedSpeed());
        float correctTotalSU = ourCapacity * ourSpeed;

        // Call parent with our corrected capacity instead of the network's wrong value
        super.updateFromNetwork(correctTotalSU, currentStress, networkSize);
    }

    // Getters for display/debug
    public int getRegularSailCount() {
        return regularSailCount;
    }

    public int getSolarSailCount() {
        return solarSailCount;
    }

    public boolean hasSkyAccess() {
        return hasSkyAccess;
    }

    public float getCurrentSolarMultiplier() {
        return getSolarMultiplier();
    }

    @Override
    public boolean addToGoggleTooltip(List<net.minecraft.network.chat.Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        // Show generator stats even when not running (no sails attached)
        // This matches the behavior of showing "0su" when no contraption
        if (!running && !added) {
            com.simibubi.create.foundation.utility.CreateLang.translate("gui.goggles.generator_stats")
                    .forGoggles(tooltip);
            com.simibubi.create.foundation.utility.CreateLang.translate("tooltip.capacityProvided")
                    .style(net.minecraft.ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            com.simibubi.create.foundation.utility.CreateLang.number(0)
                    .translate("generic.unit.stress")
                    .style(net.minecraft.ChatFormatting.AQUA)
                    .space()
                    .add(com.simibubi.create.foundation.utility.CreateLang.translate("gui.goggles.at_current_speed")
                            .style(net.minecraft.ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            return true;
        }

        return added;
    }
}
