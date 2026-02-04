package com.createphotomovement.content.kinetics.solarwindmill;

import java.util.List;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
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

    public SolarWindmillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    public void solarTick() {
        if (level == null || level.isClientSide)
            return;

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
        if (movedContraption != null && movedContraption.getContraption() instanceof SolarBearingContraption sbc) {
            this.solarSailCount = sbc.getSolarSailBlocks();
            this.regularSailCount = sbc.getRegularSailBlocks();
            this.hasSkyAccess = sbc.hasSkyAccess();
        } else if (movedContraption != null
                && movedContraption.getContraption() instanceof BearingContraption bc) {
            // Fallback for regular BearingContraption
            this.regularSailCount = bc.getSailBlocks();
            this.solarSailCount = 0;
            this.hasSkyAccess = false;
        }
        super.updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (!running)
            return 0;
        if (movedContraption == null)
            return lastGeneratedSpeed;

        // RPM is calculated normally - no solar bonus on speed
        int totalSails = regularSailCount + solarSailCount;
        int sailsPerRPM = AllConfigs.server().kinetics.windmillSailsPerRPM.get();
        int rpm = totalSails / sailsPerRPM;

        return Mth.clamp(rpm, 1, 16) * getAngleSpeedDirection();
    }

    /**
     * Gets the solar multiplier for SU calculation.
     * - 2.0 = Full solar bonus (clear day with sky access)
     * - 1.5 = Rain bonus (rainy day with sky access)
     * - 1.0 = No bonus (night, thunder, no sky access)
     */
    private float getSolarMultiplier() {
        if (!hasSkyAccess)
            return 1.0f;
        if (isNight())
            return 1.0f;
        if (isThundering())
            return 1.0f;
        if (isRaining())
            return 1.5f; // Reduced bonus during rain
        return 2.0f; // Full solar bonus
    }

    @Override
    public float calculateAddedStressCapacity() {
        // Calculate SU separately for normal and solar sails using power brackets
        // Formula: floor(sailCount / 8) * 512
        int sailsPerBracket = 8;
        float suPerBracket = 512f;

        // Normal sails: standard bracket calculation
        int normalBrackets = regularSailCount / sailsPerBracket;
        float normalSU = normalBrackets * suPerBracket;

        // Solar sails: bracket calculation with solar multiplier
        int solarBrackets = solarSailCount / sailsPerBracket;
        float solarMultiplier = getSolarMultiplier();
        float solarSU = solarBrackets * suPerBracket * solarMultiplier;

        // Total SU is the sum of both
        float totalSU = normalSU + solarSU;

        // Create multiplies capacity by speed internally, so divide by RPM
        // to get the correct displayed value matching the wiki table
        int totalSails = regularSailCount + solarSailCount;
        int rpm = Math.max(1, totalSails / sailsPerBracket);

        return totalSU / rpm;
    }

    /**
     * Called during contraption assembly to set sail counts and sky access.
     */
    public void setSailCounts(int regular, int solar, boolean skyAccess) {
        this.regularSailCount = regular;
        this.solarSailCount = solar;
        this.hasSkyAccess = skyAccess;
    }

    private boolean isNight() {
        if (level == null)
            return true;
        long dayTime = level.getDayTime() % 24000;
        return dayTime >= 13000 && dayTime < 23000;
    }

    private boolean isRaining() {
        if (level == null)
            return false;
        return level.isRaining() && !level.isThundering();
    }

    private boolean isThundering() {
        if (level == null)
            return false;
        return level.isThundering();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("RegularSails", regularSailCount);
        compound.putInt("SolarSails", solarSailCount);
        compound.putBoolean("HasSkyAccess", hasSkyAccess);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        regularSailCount = compound.getInt("RegularSails");
        solarSailCount = compound.getInt("SolarSails");
        hasSkyAccess = compound.getBoolean("HasSkyAccess");
        super.read(compound, registries, clientPacket);
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
