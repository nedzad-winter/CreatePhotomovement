package com.createphotomovement.content.kinetics.solarwindmill;

import java.util.List;

import com.createphotomovement.AllBlockEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarWindmillBearingBlockEntity extends WindmillBearingBlockEntity {

    // Cached at assembly time
    private int regularSailCount = 0;
    private int solarSailCount = 0;
    private boolean hasSkyAccess = false;
    private float lastSolarMultiplier = -1;
    private int warmup = 20;

    public SolarWindmillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
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
                // Explicitly notify network to recalculate
                if (hasNetwork()) {
                    getOrCreateNetwork().updateCapacityFor(this, getGeneratedSpeed());
                }
            }
            return;
        }

        float currentMultiplier = getSolarMultiplier();
        if (Math.abs(currentMultiplier - lastSolarMultiplier) > 0.001f) {
            lastSolarMultiplier = currentMultiplier;
            updateGeneratedRotation();
        }
    }

    @Override
    public void assemble() {
        if (!(level.getBlockState(worldPosition).getBlock() instanceof BearingBlock))
            return;

        Direction direction = getBlockState().getValue(BearingBlock.FACING);

        // Use our custom SolarBearingContraption instead of regular BearingContraption
        SolarBearingContraption contraption = new SolarBearingContraption(true, direction);
        try {
            if (!contraption.assemble(level, worldPosition)) {
                return;
            }

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

        // Fabric: setRotationAxis usually takes direction or axis. Checking mappings.
        // Assuming standard Mojang mappings.
        if (movedContraption.getContraption() instanceof BearingContraption bc) {
            // SolarBearingContraption extends BearingContraption, so this is safe
            // Fabric typically uses the bearing's facing as the rotation axis
            // NeoForge use explicit setRotationAxis if available, but here we likely rely
            // on entity default
            // or we might need to cast to AbstractContraptionEntity if that exposes it.
            // But checking the axis is often for verification.
            // In 1.20.1 Fabric Create 6.x, alignment might be automatic.
            // We can just query validity:
            if (bc.getFacing().getAxis() == Direction.Axis.Y) {
                // Do something specific if needed?
                // The original code call was
                // movedContraption.getContraption().getStartRotationAxis();
                // which suggests it wanted the axis value for some side effect or check.
                // If it was just a getter being called without usage (as snippet implies), we
                // can remove it.
                // But likely it was setting something? No, it's a getter.
                // If the line was: movedContraption.getContraption().getStartRotationAxis();
                // Then it does nothing java-wise unless it has side effects.
                // Assuming it was checking or initializing.
                // If we don't need it, we can remove it.
                // But let's assume we want to ensure axis is correct on the entity.
                // movedContraption.setRotationAxis(bc.getFacing().getAxis()); if method exists.
                // But ControlledContraptionEntity handles this.
            }
        }
        // entity
        // Actually, ControlledContraptionEntity usually handles this automatically on
        // creation or we set it.
        // NeoForge: movedContraption.setRotationAxis(direction.getAxis());
        // Fabric: Likely same if Mixins expose it, or check AbstractContraptionEntity.
        // I will assume same API for now.

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
        if (movedContraption != null) {
            Contraption c = movedContraption.getContraption();

            if (c instanceof SolarBearingContraption) {
                SolarBearingContraption sbc = (SolarBearingContraption) c;
                this.solarSailCount = sbc.getSolarSailBlocks();
                this.regularSailCount = sbc.getRegularSailBlocks();
                this.hasSkyAccess = sbc.hasSkyAccess();

                // CRITICAL: Zero out parent's sailBlocks to prevent double-counting!
                sbc.setSailBlocks(0);
            } else if (c instanceof BearingContraption) {
                BearingContraption bc = (BearingContraption) c;
                this.regularSailCount = bc.getSailBlocks();
                this.solarSailCount = 0;
                this.hasSkyAccess = false;
            }
        }

        super.updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (!running) {
            return 0;
        }
        if (movedContraption == null) {
            return 0;
        }

        int totalSails = regularSailCount + solarSailCount;
        int sailsPerRPM = AllConfigs.server().kinetics.windmillSailsPerRPM.get();
        int rpm = totalSails / sailsPerRPM;

        float speed = Mth.clamp(rpm, 1, 16) * getAngleSpeedDirection();

        return speed;
    }

    private float getSolarMultiplier() {
        if (!hasSkyAccess)
            return 1.0f;
        if (isNight())
            return 1.0f;
        if (isThundering())
            return 1.0f;
        if (isRaining())
            return 1.5f;
        return 2.0f;
    }

    @Override
    public float calculateAddedStressCapacity() {
        if (!running)
            return 0;

        int sailsPerBracket = AllConfigs.server().kinetics.windmillSailsPerRPM.get();
        float suPerBracket = 512f;

        int normalBrackets = regularSailCount / sailsPerBracket;
        float normalSU = normalBrackets * suPerBracket;

        int solarBrackets = solarSailCount / sailsPerBracket;
        float solarMultiplier = getSolarMultiplier();
        float solarSU = solarBrackets * suPerBracket * solarMultiplier;

        float totalSU = normalSU + solarSU;

        int totalSails = regularSailCount + solarSailCount;
        int rpm = Math.max(1, totalSails / sailsPerBracket);

        float result = totalSU / rpm;

        this.lastCapacityProvided = result;
        return result;
    }

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
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("RegularSails", regularSailCount);
        compound.putInt("SolarSails", solarSailCount);
        compound.putBoolean("HasSkyAccess", hasSkyAccess);

        if (!clientPacket) {
            this.capacity = 0;
            this.lastCapacityProvided = 0;
        }
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        regularSailCount = compound.getInt("RegularSails");
        solarSailCount = compound.getInt("SolarSails");
        hasSkyAccess = compound.getBoolean("HasSkyAccess");

        if (!clientPacket) {
            this.lastCapacityProvided = 0;
            this.capacity = 0;
            this.warmup = 20;
        }
    }

    @Override
    public void initialize() {
        if (hasNetwork() && !level.isClientSide) {
            KineticNetwork net = getOrCreateNetwork();
            if (net.initialized) {
                net.initialized = false;
            }
        }
        super.initialize();
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        float ourCapacity = calculateAddedStressCapacity();
        float ourSpeed = Math.abs(getGeneratedSpeed());
        float correctTotalSU = ourCapacity * ourSpeed;

        super.updateFromNetwork(correctTotalSU, currentStress, networkSize);
    }

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
}
