package com.createphotomovement.gametest;

import com.createphotomovement.content.kinetics.solarwindmill.SolarWindmillBearingBlockEntity;
import com.createphotomovement.logic.DayCycle;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared setup and assertions for the solar windmill bearing.
 *
 * <p>
 * Split out from {@link SolarAssertions} because a windmill is a different kind
 * of thing to check: the panels are single blocks that answer questions about
 * the sky directly, while the bearing only knows what it counted at assembly
 * time and keeps it in fields. Most of the interesting failures are about those
 * fields going stale or being counted twice, so the diagnostics here print them.
 *
 * <p>
 * Same import rule as the rest of {@code common/}: only {@code net.minecraft.*}
 * and {@code com.simibubi.create.*}. The bearing block entity itself still
 * exists once per target, but all three expose the same public methods, so
 * naming it here compiles everywhere.
 */
public final class WindmillAssertions {

    /**
     * Edge length of the sail plane, giving {@code EDGE * EDGE} sails.
     *
     * <p>
     * Three, so nine -- above Create's minimum of eight, below which the
     * contraption refuses to assemble at all. Deliberately not a round bracket
     * multiple: the sail-per-RPM division truncates, and a count that divides
     * evenly would hide it.
     */
    public static final int SAIL_EDGE = 3;

    /** Sails per plane, derived so a caller never has to square it themselves. */
    public static final int SAIL_COUNT = SAIL_EDGE * SAIL_EDGE;

    /** The multiplier a clear day with sky access is meant to give. */
    public static final float BONUS_CLEAR = 2.0F;

    /** Rain keeps a reduced bonus. */
    public static final float BONUS_RAIN = 1.5F;

    /** Night, thunder and a blocked sky all fall back to no bonus at all. */
    public static final float BONUS_NONE = 1.0F;

    private WindmillAssertions() {
    }

    // ---------------------------------------------------------------- building

    /**
     * Places a bearing facing north with a sail plane on its anchor.
     *
     * <p>
     * The plane sits one block north of the bearing and is centred on it, so the
     * anchor block the contraption starts its search from is itself a sail. Sails
     * are placed before the bearing so the bearing's first tick already sees a
     * complete structure.
     *
     * <p>
     * The sails are given the same facing as the bearing, and that is not
     * cosmetic. A sail only counts as attached to the sails beside it if they lie
     * in its own plane, so a plane of default-facing sails hanging off a
     * north-facing bearing breaks into strips: assembly then fails with "not
     * enough sail-like blocks", quoting a count far below what was placed.
     *
     * @param sail which sail to fill the plane with -- a solar sail or a plain
     *             Create one, which is the whole point of most of these tests
     */
    public static void buildWindmill(GameTestHelper helper, BlockPos bearingPos, Block bearing, Block sail) {
        BlockPos anchor = bearingPos.north();
        BlockState sailState = sail.defaultBlockState().setValue(BearingBlock.FACING, Direction.NORTH);
        int reach = SAIL_EDGE / 2;
        for (int dx = -reach; dx <= reach; dx++)
            for (int dy = -reach; dy <= reach; dy++)
                helper.setBlock(anchor.offset(dx, dy, 0), sailState);

        BlockState state = bearing.defaultBlockState().setValue(BearingBlock.FACING, Direction.NORTH);
        helper.setBlock(bearingPos, state);
    }

    /**
     * Roofs over the area the bearing checks for sky access.
     *
     * <p>
     * {@code SolarBearingContraption.checkSkyAccess} scans a five by five area one
     * block above the bearing and is satisfied if <em>any</em> of those positions
     * sees sky -- that tolerance is what lets a shaft or a support block sit next
     * to the bearing. So taking the sky away means covering all twenty-five of
     * them, not just the one overhead.
     *
     * @param roof placed two above the bearing, clear of the sail plane
     */
    public static void roofSkyAccess(GameTestHelper helper, BlockPos bearingPos, Block roof) {
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                helper.setBlock(bearingPos.offset(dx, 2, dz), roof);
    }

    /**
     * Assembles the contraption.
     *
     * <p>
     * Called directly rather than through a lever or a redstone pulse. It is the
     * same method the block's ticker calls, and it keeps the arena free of wiring
     * that would need its own sky-access exception.
     */
    public static void assemble(GameTestHelper helper, BlockPos bearingPos) {
        bearingAt(helper, bearingPos).assemble();
    }

    public static void disassemble(GameTestHelper helper, BlockPos bearingPos) {
        bearingAt(helper, bearingPos).disassemble();
    }

    // ---------------------------------------------------------------- inspection

    public static SolarWindmillBearingBlockEntity bearingAt(GameTestHelper helper, BlockPos bearingPos) {
        var be = helper.getBlockEntity(bearingPos);
        if (be == null)
            throw new GameTestAssertException("No block entity at " + bearingPos
                    + " -- the bearing was never placed, or the structure template overwrote it");
        if (!(be instanceof SolarWindmillBearingBlockEntity bearing))
            throw new GameTestAssertException("Block entity at " + bearingPos + " is a "
                    + be.getClass().getSimpleName() + ", not a solar windmill bearing");
        return bearing;
    }

    public static float speedAt(GameTestHelper helper, BlockPos bearingPos) {
        return bearingAt(helper, bearingPos).getGeneratedSpeed();
    }

    /** Stress capacity per RPM, as the bearing reports it to the network. */
    public static float capacityAt(GameTestHelper helper, BlockPos bearingPos) {
        return bearingAt(helper, bearingPos).calculateAddedStressCapacity();
    }

    /**
     * Total capacity of the kinetic network the bearing belongs to.
     *
     * <p>
     * The distinction from {@link #capacityAt} matters. That one recomputes from
     * the bearing's current fields and is therefore always self-consistent; this
     * one is {@code presentCapacity + unloadedCapacity}, which is exactly where a
     * bearing counted both as a live source and as leftover unloaded capacity
     * would show up twice. The reported doubling bug lives in this number, not the
     * other one.
     */
    public static float networkCapacityAt(GameTestHelper helper, BlockPos bearingPos) {
        KineticBlockEntity be = bearingAt(helper, bearingPos);
        if (!be.hasNetwork())
            throw new GameTestAssertException("The bearing at " + bearingPos + " has no kinetic network"
                    + describeBearing(helper, bearingPos));
        KineticNetwork network = be.getOrCreateNetwork();
        return network.calculateCapacity();
    }

    /**
     * Everything the bearing believes about itself, appended to every failure.
     *
     * <p>
     * A bearing that reports the wrong capacity is almost always reporting the
     * right formula over the wrong sail counts, so the counts have to be visible.
     */
    public static String describeBearing(GameTestHelper helper, BlockPos bearingPos) {
        SolarWindmillBearingBlockEntity bearing = bearingAt(helper, bearingPos);
        // The assembly exception is the single most useful field here: a bearing that
        // refused to assemble looks identical to one that was never told to, and only
        // this says which.
        AssemblyException failure = bearing.getLastAssemblyException();
        return String.format(
                " [running=%b, regularSails=%d, solarSails=%d, skyAccess=%b, multiplier=%.2f, speed=%.2f,"
                        + " capacityPerRpm=%.2f, dayTime=%d, raining=%b, thundering=%b, assemblyError=%s]",
                bearing.isRunning(), bearing.getRegularSailCount(), bearing.getSolarSailCount(),
                bearing.hasSkyAccess(), bearing.getCurrentSolarMultiplier(), bearing.getGeneratedSpeed(),
                bearing.calculateAddedStressCapacity(), DayCycle.timeOfDay(helper.getLevel().getDayTime()),
                helper.getLevel().isRaining(), helper.getLevel().isThundering(),
                failure == null ? "none" : failure.component.getString());
    }

    // ---------------------------------------------------------------- assertions

    public static void assertRunning(GameTestHelper helper, BlockPos bearingPos, String why) {
        SolarWindmillBearingBlockEntity bearing = bearingAt(helper, bearingPos);
        if (!bearing.isRunning())
            throw new GameTestAssertException(why + ": the bearing did not assemble"
                    + describeBearing(helper, bearingPos));
        if (bearing.getMovedContraption() == null)
            throw new GameTestAssertException(why + ": the bearing reports running but has no contraption"
                    + describeBearing(helper, bearingPos));
    }

    public static void assertSailCounts(GameTestHelper helper, BlockPos bearingPos, int regular, int solar,
            String why) {
        SolarWindmillBearingBlockEntity bearing = bearingAt(helper, bearingPos);
        if (bearing.getRegularSailCount() != regular || bearing.getSolarSailCount() != solar)
            throw new GameTestAssertException(why + ": expected " + regular + " regular and " + solar
                    + " solar sails" + describeBearing(helper, bearingPos));
    }

    public static void assertSkyAccess(GameTestHelper helper, BlockPos bearingPos, boolean expected, String why) {
        if (bearingAt(helper, bearingPos).hasSkyAccess() != expected)
            throw new GameTestAssertException(why + ": expected sky access to be " + expected
                    + describeBearing(helper, bearingPos));
    }

    /**
     * Asserts the bearing turns at all.
     *
     * <p>
     * Its own assertion rather than a capacity check, because the rule that makes
     * this mod's windmill different is precisely that rotation and bonus are
     * independent: at night the bonus goes and the rotation stays.
     */
    public static void assertTurning(GameTestHelper helper, BlockPos bearingPos, String why) {
        if (speedAt(helper, bearingPos) == 0)
            throw new GameTestAssertException(why + ": expected the bearing to keep turning, but it is stopped"
                    + describeBearing(helper, bearingPos));
    }

    public static void assertStopped(GameTestHelper helper, BlockPos bearingPos, String why) {
        float speed = speedAt(helper, bearingPos);
        if (speed != 0)
            throw new GameTestAssertException(why + ": expected no rotation, but the bearing turns at " + speed
                    + " RPM" + describeBearing(helper, bearingPos));
    }

    public static void assertSameSpeed(GameTestHelper helper, BlockPos a, BlockPos b, String why) {
        float speedA = speedAt(helper, a);
        float speedB = speedAt(helper, b);
        if (Math.abs(speedA - speedB) > 0.001F)
            throw new GameTestAssertException(why + ": expected the same RPM, but " + a + " turns at " + speedA
                    + " and " + b + " at " + speedB + describeBearing(helper, a) + describeBearing(helper, b));
    }

    /**
     * Asserts the solar bearing provides {@code factor} times what an otherwise
     * identical bearing with plain sails provides.
     *
     * <p>
     * Expressed as a ratio against a live reference rather than an absolute number
     * of stress units, so that changing the sails-per-RPM config -- or the base
     * capacity Create hands out per bracket -- moves both sides and the test keeps
     * testing the bonus rather than the configuration.
     */
    public static void assertBonusFactor(GameTestHelper helper, BlockPos solar, BlockPos plain, float factor,
            String why) {
        float solarCapacity = capacityAt(helper, solar);
        float plainCapacity = capacityAt(helper, plain);
        if (plainCapacity == 0)
            throw new GameTestAssertException(why + ": the reference windmill provides no capacity at all, so "
                    + "there is nothing to compare against" + describeBearing(helper, plain));
        float actual = solarCapacity / plainCapacity;
        if (Math.abs(actual - factor) > 0.01F)
            throw new GameTestAssertException(why + ": expected the solar bearing to provide " + factor
                    + " times the plain one, but got " + actual + " (" + solarCapacity + " vs " + plainCapacity
                    + " SU/RPM)" + describeBearing(helper, solar) + describeBearing(helper, plain));
    }

    /**
     * Asserts a network capacity, calling out growth specifically.
     *
     * <p>
     * A value that came back too high after a reload is the shape the original bug
     * had, and it reads very differently from a value that is simply wrong.
     */
    public static void assertNetworkCapacity(GameTestHelper helper, BlockPos bearingPos, float expected,
            String why) {
        float actual = networkCapacityAt(helper, bearingPos);
        if (Math.abs(actual - expected) <= 0.01F)
            return;
        String hint = "";
        if (actual > expected) {
            float ratio = expected == 0 ? 0 : actual / expected;
            hint = " -- it grew by a factor of " + ratio
                    + ", so the bearing is being counted more than once in the network";
        }
        throw new GameTestAssertException(why + ": expected a network capacity of " + expected + " but got "
                + actual + hint + describeBearing(helper, bearingPos));
    }
}
