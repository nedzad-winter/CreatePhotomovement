package com.createphotomovement.gametest;

import com.createphotomovement.logic.DayCycle;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;

/**
 * Assertions shared by the three loaders' game tests.
 *
 * <p>
 * The test classes themselves cannot be shared: NeoForge wants static methods
 * annotated with {@code @GameTest}, Fabric wants instance methods implementing
 * {@code FabricGameTest}, and the two annotations live in different packages.
 * What <em>can</em> be shared is everything below the annotation -- the world
 * setup and the checks -- which is where the behaviour being compared actually
 * lives.
 *
 * <p>
 * Only {@code net.minecraft.*} and {@code com.simibubi.create.*} imports are
 * allowed here, same as the rest of {@code common/}.
 */
public final class SolarAssertions {

    /** Roughly midday: full sun, well clear of both dawn and dusk. */
    public static final long MIDDAY = 6000L;

    /** Roughly midnight, comfortably inside the night window. */
    public static final long MIDNIGHT = 18000L;

    private SolarAssertions() {
    }

    // ---------------------------------------------------------------- world setup

    /**
     * Sets the time of day for the whole level.
     *
     * <p>
     * Game tests share one level, so this leaks between tests in the same batch.
     * Every test that cares about the time must set it rather than assume it.
     */
    public static void setDayTime(GameTestHelper helper, long dayTime) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(dayTime);
    }

    /** Clear weather. */
    public static void setClear(GameTestHelper helper) {
        helper.getLevel().setWeatherParameters(6000, 0, false, false);
    }

    /** Rain without thunder. */
    public static void setRaining(GameTestHelper helper) {
        helper.getLevel().setWeatherParameters(0, 6000, true, false);
    }

    /** A full thunderstorm: Minecraft sets both the rain and thunder flags. */
    public static void setThundering(GameTestHelper helper) {
        helper.getLevel().setWeatherParameters(0, 6000, true, true);
    }

    // ---------------------------------------------------------------- inspection

    /**
     * The generated speed of the kinetic block entity at the given relative
     * position, or a failure if there is no kinetic block entity there.
     */
    public static float generatedSpeedAt(GameTestHelper helper, BlockPos relativePos) {
        KineticBlockEntity be = kineticAt(helper, relativePos);
        return be.getGeneratedSpeed();
    }

    /** The stress capacity the block entity contributes, per RPM. */
    public static float stressCapacityAt(GameTestHelper helper, BlockPos relativePos) {
        return kineticAt(helper, relativePos).calculateAddedStressCapacity();
    }

    private static KineticBlockEntity kineticAt(GameTestHelper helper, BlockPos relativePos) {
        var be = helper.getBlockEntity(relativePos);
        if (be == null)
            throw new GameTestAssertException("No block entity at " + relativePos
                    + " -- is the structure template missing the generator?");
        if (!(be instanceof KineticBlockEntity kinetic))
            throw new GameTestAssertException("Block entity at " + relativePos + " is a "
                    + be.getClass().getSimpleName() + ", not a KineticBlockEntity");
        return kinetic;
    }

    // ---------------------------------------------------------------- assertions

    public static void assertGenerating(GameTestHelper helper, BlockPos relativePos, String why) {
        float speed = generatedSpeedAt(helper, relativePos);
        if (speed == 0)
            throw new GameTestAssertException(why + ": expected the generator to produce rotation, but it is idle");
    }

    public static void assertIdle(GameTestHelper helper, BlockPos relativePos, String why) {
        float speed = generatedSpeedAt(helper, relativePos);
        if (speed != 0)
            throw new GameTestAssertException(why + ": expected no rotation, but the generator produces " + speed
                    + " RPM");
    }

    public static void assertSpeed(GameTestHelper helper, BlockPos relativePos, float expected, String why) {
        float actual = generatedSpeedAt(helper, relativePos);
        if (Math.abs(actual - expected) > 0.001f)
            throw new GameTestAssertException(why + ": expected " + expected + " RPM but got " + actual);
    }

    /**
     * Asserts that one generator out-produces another under whatever conditions the
     * caller has set up. Used to check that the advanced variants really are an
     * upgrade, without hard-coding numbers that the config can change.
     */
    public static void assertFasterThan(GameTestHelper helper, BlockPos faster, BlockPos slower, String why) {
        float fast = Math.abs(generatedSpeedAt(helper, faster));
        float slow = Math.abs(generatedSpeedAt(helper, slower));
        if (fast <= slow)
            throw new GameTestAssertException(why + ": expected " + faster + " to out-produce " + slower
                    + ", but got " + fast + " vs " + slow + " RPM");
    }

    /**
     * Asserts two generators behave identically. This is what protects the later
     * AllBlocks refactor: a dyed variant must not drift from the undyed one.
     */
    public static void assertSameSpeed(GameTestHelper helper, BlockPos a, BlockPos b, String why) {
        float speedA = generatedSpeedAt(helper, a);
        float speedB = generatedSpeedAt(helper, b);
        if (Math.abs(speedA - speedB) > 0.001f)
            throw new GameTestAssertException(why + ": expected " + a + " and " + b
                    + " to behave identically, but got " + speedA + " vs " + speedB + " RPM");
    }

    /**
     * Fails if the test arena has no sky above the given position.
     *
     * <p>
     * Worth calling explicitly at the start of any sky-dependent test: if the
     * structure template has a ceiling, every solar test fails with "produces
     * nothing" and the real cause is easy to miss.
     */
    public static void assertHasSkyAccess(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absolute = helper.absolutePos(relativePos);
        if (!helper.getLevel().canSeeSky(absolute))
            throw new GameTestAssertException("The test arena has no sky above " + relativePos
                    + ". The structure template probably has a ceiling -- fix the template rather than the "
                    + "expectations, otherwise every solar test fails for the wrong reason.");
    }

    /** Sanity check that the helpers above agree with the shared arithmetic. */
    public static void assertIsNight(GameTestHelper helper) {
        long time = helper.getLevel().getDayTime();
        if (!DayCycle.isNight(time))
            throw new GameTestAssertException("Expected night but the level is at day time " + DayCycle.timeOfDay(time));
    }
}
