package com.createphotomovement.gametest;

import com.createphotomovement.AllBlocks;
import com.createphotomovement.CreatePhotomovement;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * In-game tests for the solar windmill bearing on NeoForge 1.21.1.
 *
 * <p>
 * The bearing is the part of this mod with the most hand-written state: sail
 * counts and sky access are decided once at assembly time and then cached in
 * fields, written to NBT, deliberately zeroed again on load, and recalculated
 * from the contraption's block list. Every one of those steps exists because of
 * a capacity that was once counted twice. The unit tests cover the arithmetic;
 * only a world can say whether the right numbers reach it.
 *
 * <p>
 * The rule these tests exist to protect, stated once:
 * <strong>at night the bearing keeps turning.</strong> Only the solar bonus
 * disappears -- at night it is an ordinary windmill of the same sail count. A
 * test that expected it to stop would pass against broken behaviour.
 */
@GameTestHolder(CreatePhotomovement.MOD_ID)
@PrefixGameTestTemplate(false)
public class SolarWindmillGameTests {

    private static final String PLATFORM = "solar_platform";

    /**
     * The bearing under test. Y 4 leaves room for the sail plane to hang one block
     * below it without reaching the floor, which the template places at relative
     * Y 1.
     */
    private static final BlockPos BEARING = new BlockPos(3, 4, 4);

    /**
     * An identical bearing carrying plain Create sails.
     *
     * <p>
     * Nearly every check here is a comparison against this one rather than against
     * a hard-coded stress figure. The behaviour being tested is "solar sails are
     * worth a multiple of ordinary sails", and a reference windmill states that
     * directly while surviving any config change to sails-per-RPM.
     */
    private static final BlockPos REFERENCE = new BlockPos(10, 4, 4);

    /** Ticks to let the bearing finish its ten-tick warmup and settle. */
    private static final int SETTLE = 15;

    // ------------------------------------------------------------ assembly

    @GameTest(template = PLATFORM, batch = "windmill_day")
    public static void assemblesFromSolarSails(GameTestHelper helper) {
        dayWithSun(helper);
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), solarSail());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assemble(helper, BEARING))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assertRunning(helper, BEARING, "Nine solar sails under open sky");
                    // The counts are what every capacity figure downstream is derived from,
                    // so they get their own assertion rather than being inferred from an SU
                    // value that could be right for the wrong reason.
                    WindmillAssertions.assertSailCounts(helper, BEARING, 0, WindmillAssertions.SAIL_COUNT,
                            "Solar sails must be counted as solar, not as regular sails");
                    WindmillAssertions.assertSkyAccess(helper, BEARING, true, "Open sky above the bearing");
                    WindmillAssertions.assertTurning(helper, BEARING, "An assembled windmill");
                })
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "windmill_day")
    public static void plainSailsAreNotCountedAsSolar(GameTestHelper helper) {
        dayWithSun(helper);
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), plainSail());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assemble(helper, BEARING))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertSailCounts(helper, BEARING,
                        WindmillAssertions.SAIL_COUNT, 0,
                        "Create's own sails on a solar bearing must count as regular"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ the solar bonus

    @GameTest(template = PLATFORM, batch = "windmill_day")
    public static void solarSailsDoubleTheCapacityByDay(GameTestHelper helper) {
        dayWithSun(helper);
        buildPair(helper);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertBonusFactor(helper, BEARING, REFERENCE,
                        WindmillAssertions.BONUS_CLEAR, "Clear midday with sky access"))
                .thenSucceed();
    }

    /**
     * The bonus is on stress, not on speed. Two windmills with the same number of
     * sails must turn at the same rate whichever kind of sail they carry.
     */
    @GameTest(template = PLATFORM, batch = "windmill_day")
    public static void sailTypeDoesNotChangeSpeed(GameTestHelper helper) {
        dayWithSun(helper);
        buildPair(helper);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertSameSpeed(helper, BEARING, REFERENCE,
                        "Solar sails must not add RPM, only stress capacity"))
                .thenSucceed();
    }

    /**
     * Sky access is decided once, at assembly time, over a five by five area. A
     * bearing built under a roof gets no bonus even at midday.
     */
    @GameTest(template = PLATFORM, batch = "windmill_day")
    public static void noSkyAccessMeansNoBonus(GameTestHelper helper) {
        dayWithSun(helper);
        buildPair(helper);
        WindmillAssertions.roofSkyAccess(helper, BEARING, Blocks.STONE);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assertSkyAccess(helper, BEARING, false, "Roofed bearing");
                    WindmillAssertions.assertBonusFactor(helper, BEARING, REFERENCE,
                            WindmillAssertions.BONUS_NONE,
                            "A roofed solar windmill is worth exactly a plain one");
                    // Still a windmill, though: the roof takes the bonus, not the wind.
                    WindmillAssertions.assertTurning(helper, BEARING, "A roofed windmill");
                })
                .thenSucceed();
    }

    // ------------------------------------------------------------ night

    /**
     * The rule this whole class exists for. Assembled and measured at night: the
     * bearing turns, and it is worth exactly what plain sails are worth.
     */
    @GameTest(template = PLATFORM, batch = "windmill_night")
    public static void keepsTurningAtNight(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT);
        SolarAssertions.setClear(helper);
        buildPair(helper);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    SolarAssertions.assertIsNight(helper);
                    WindmillAssertions.assertTurning(helper, BEARING,
                            "At night the solar windmill is an ordinary windmill -- it must not stop");
                    WindmillAssertions.assertSameSpeed(helper, BEARING, REFERENCE,
                            "At night it must turn at exactly a plain windmill's speed");
                })
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "windmill_night")
    public static void losesOnlyTheBonusAtNight(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT);
        SolarAssertions.setClear(helper);
        buildPair(helper);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertBonusFactor(helper, BEARING, REFERENCE,
                        WindmillAssertions.BONUS_NONE,
                        "At night solar sails are worth the same as the same number of plain sails"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ weather

    /**
     * Rain is testable here even though it is not for the panels.
     *
     * <p>
     * The bearing reads {@code level.isRaining()}, which is global weather and can
     * simply be set. The panels read {@code level.isRainingAt(pos)}, which also
     * consults the heightmap and always reports "covered" inside a test arena.
     * Same weather, two different questions -- see docs/test-world.md.
     */
    @GameTest(template = PLATFORM, batch = "windmill_rain")
    public static void rainReducesTheBonus(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setRaining(helper);
        buildPair(helper);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertBonusFactor(helper, BEARING, REFERENCE,
                        WindmillAssertions.BONUS_RAIN, "Daytime rain keeps a reduced bonus"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "windmill_thunder")
    public static void thunderRemovesTheBonus(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setThundering(helper);
        buildPair(helper);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assemblePair(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assertBonusFactor(helper, BEARING, REFERENCE,
                            WindmillAssertions.BONUS_NONE, "A thunderstorm removes the bonus entirely");
                    WindmillAssertions.assertTurning(helper, BEARING, "A windmill in a thunderstorm");
                })
                .thenSucceed();
    }

    // ------------------------------------------------------------ stability

    /**
     * The reported bug, as far as a game test can reach it: capacity accumulating
     * instead of being replaced.
     *
     * <p>
     * Measured on the network total rather than on the bearing's own figure. The
     * bearing recomputes its contribution from scratch every time it is asked, so
     * it can never disagree with itself; the network adds up live sources and
     * leftover unloaded capacity, and it was the network that once counted this
     * bearing in both. Restarting the server is still out of reach -- that is
     * manual check C in docs/test-world.md.
     */
    @GameTest(template = PLATFORM, timeoutTicks = 400, batch = "windmill_cycle")
    public static void networkCapacityReturnsAfterANightDayCycle(GameTestHelper helper) {
        dayWithSun(helper);
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), solarSail());

        float[] baseline = new float[1];

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assemble(helper, BEARING))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    baseline[0] = WindmillAssertions.networkCapacityAt(helper, BEARING);
                    // Without this the test would pass on a network that never had any
                    // capacity to lose: zero comes back as zero however badly the reload
                    // path behaves.
                    if (baseline[0] <= 0)
                        helper.fail("The network reports no capacity at all before the cycle even starts, so "
                                + "there is nothing for this test to watch"
                                + WindmillAssertions.describeBearing(helper, BEARING));
                })
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertNetworkCapacity(helper, BEARING, baseline[0],
                        "Network capacity after one night/day cycle"))
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertNetworkCapacity(helper, BEARING, baseline[0],
                        "Network capacity after two night/day cycles"))
                .thenSucceed();
    }

    /**
     * Speed must survive the same cycle untouched -- not merely be non-zero at each
     * end, but be the same number it started at.
     */
    @GameTest(template = PLATFORM, timeoutTicks = 400, batch = "windmill_cycle_speed")
    public static void speedIsUnaffectedByNightfall(GameTestHelper helper) {
        dayWithSun(helper);
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), solarSail());

        float[] byDay = new float[1];

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assemble(helper, BEARING))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assertTurning(helper, BEARING, "Before nightfall");
                    byDay[0] = WindmillAssertions.speedAt(helper, BEARING);
                })
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    float atNight = WindmillAssertions.speedAt(helper, BEARING);
                    if (atNight != byDay[0])
                        helper.fail("Nightfall changed the rotation from " + byDay[0] + " to " + atNight
                                + " RPM. Only the stress bonus may depend on the time of day."
                                + WindmillAssertions.describeBearing(helper, BEARING));
                })
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    float backByDay = WindmillAssertions.speedAt(helper, BEARING);
                    if (backByDay != byDay[0])
                        helper.fail("Dawn changed the rotation from " + byDay[0] + " to " + backByDay + " RPM"
                                + WindmillAssertions.describeBearing(helper, BEARING));
                })
                .thenSucceed();
    }

    /**
     * The other half of the ghost-capacity problem: a bearing that has let go of
     * its contraption must stop claiming to provide anything.
     */
    @GameTest(template = PLATFORM, batch = "windmill_disassemble")
    public static void disassemblingClearsTheOutput(GameTestHelper helper) {
        dayWithSun(helper);
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), solarSail());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assemble(helper, BEARING))
                .thenIdle(SETTLE)
                .thenExecute(() -> WindmillAssertions.assertTurning(helper, BEARING, "Before disassembly"))
                .thenExecute(() -> WindmillAssertions.disassemble(helper, BEARING))
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assertStopped(helper, BEARING, "After disassembly");
                    float capacity = WindmillAssertions.capacityAt(helper, BEARING);
                    if (capacity != 0)
                        helper.fail("A disassembled bearing still provides " + capacity + " SU/RPM"
                                + WindmillAssertions.describeBearing(helper, BEARING));
                })
                .thenSucceed();
    }

    // ------------------------------------------------------------ helpers

    private static void dayWithSun(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
    }

    /** A solar windmill and a plain-sail reference, side by side. */
    private static void buildPair(GameTestHelper helper) {
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), solarSail());
        WindmillAssertions.buildWindmill(helper, REFERENCE, bearing(), plainSail());
    }

    private static void assemblePair(GameTestHelper helper) {
        WindmillAssertions.assemble(helper, BEARING);
        WindmillAssertions.assemble(helper, REFERENCE);
    }

    private static Block bearing() {
        return AllBlocks.SOLAR_WINDMILL_BEARING.get();
    }

    private static Block solarSail() {
        return AllBlocks.SOLAR_SAIL.get();
    }

    /**
     * Create's own sail frame. It carries the same {@code windmill_sails} tag, so
     * the contraption accepts it and counts it as a regular sail -- which is what
     * makes it a fair reference.
     */
    private static Block plainSail() {
        return com.simibubi.create.AllBlocks.SAIL_FRAME.get();
    }
}
