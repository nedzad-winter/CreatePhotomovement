package com.createphotomovement.gametest;

import com.createphotomovement.AllBlocks;
import com.createphotomovement.CreatePhotomovement;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlock;
import com.createphotomovement.logic.HorizontalSolarOutput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * In-game tests for the horizontal solar generators on NeoForge 1.21.1.
 *
 * <p>
 * The panel sits at the west end of the platform facing east, leaving a clear
 * runway of fourteen blocks in front of it. That is what allows the obstruction
 * range to be tested from both sides: a block at distance 10 still counts, one
 * at distance 11 must not.
 */
@GameTestHolder(CreatePhotomovement.MOD_ID)
@PrefixGameTestTemplate(false)
public class HorizontalSolarGameTests {

    private static final String PLATFORM = "solar_platform";

    /**
     * West end of the platform, facing east down the long axis.
     *
     * <p>
     * Y is 2, not 1: a structure block places its contents one layer above itself,
     * so the template's floor lands on relative Y 1. A panel there would be staring
     * straight into that floor -- which is exactly what happened the first time and
     * made every horizontal test report zero output.
     */
    private static final BlockPos PANEL = new BlockPos(1, 2, 4);

    /** Second lane, for side-by-side comparisons. */
    private static final BlockPos REFERENCE = new BlockPos(1, 2, 7);

    private static final int SETTLE = 5;

    /** Dawn: an east-facing panel is at its peak. */
    private static final long DAWN = 0L;

    /** Dusk: an east-facing panel is at its floor, a west-facing one at its peak. */
    private static final long DUSK = 12000L;

    // ------------------------------------------------------------ sky and time

    @GameTest(template = PLATFORM, batch = "horiz_day")
    public static void generatesUnderOpenSky(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    SolarAssertions.assertHasSkyAccess(helper, PANEL.above());
                    SolarAssertions.assertGenerating(helper, PANEL, "Open sky at midday");
                })
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_night")
    public static void idleAtNight(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    SolarAssertions.assertIsNight(helper);
                    SolarAssertions.assertIdle(helper, PANEL, "Midnight");
                })
                .thenSucceed();
    }

    // ------------------------------------------------------------ obstruction

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void blockDirectlyInFrontStopsGeneration(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, DAWN);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());
        helper.setBlock(PANEL.east(1), Blocks.STONE);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertIdle(helper, PANEL,
                        "A block touching the panel face"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void clearGlassInFrontDoesNotBlock(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, DAWN);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());
        helper.setBlock(PANEL.east(1), Blocks.GLASS);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertGenerating(helper, PANEL,
                        "Clear glass touching the panel face"))
                .thenSucceed();
    }

    /** A gap of seven air blocks -- the case from the scenario list. */
    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void blockAtDistanceEightReducesOutput(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, DAWN);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());
        helper.setBlock(PANEL.east(8), Blocks.STONE);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    // Still turns -- only the capacity drops.
                    SolarAssertions.assertGenerating(helper, PANEL, "Obstruction eight blocks away");
                    assertCapacity(helper, PANEL, HorizontalSolarOutput.MIN_CAPACITY,
                            "Shaded output should sit at the floor, not at half");
                })
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void blockAtDistanceTwoReducesOutput(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, DAWN);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());
        helper.setBlock(PANEL.east(2), Blocks.STONE);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacity(helper, PANEL, HorizontalSolarOutput.MIN_CAPACITY,
                        "Obstruction at the near edge of the range"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void blockAtDistanceElevenIsIgnored(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, DAWN);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());
        helper.setBlock(PANEL.east(11), Blocks.STONE);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacityAbove(helper, PANEL, HorizontalSolarOutput.MIN_CAPACITY,
                        "An obstruction past the scan range must not shade the panel"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void clearGlassAtDistanceEightDoesNotShade(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, DAWN);
        SolarAssertions.setClear(helper);
        placePanel(helper, PANEL, Direction.EAST, basic());
        helper.setBlock(PANEL.east(8), Blocks.GLASS);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacityAbove(helper, PANEL, HorizontalSolarOutput.MIN_CAPACITY,
                        "Clear glass in the runway must not shade the panel"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ sun tracking

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void eastPeaksAtDawn(GameTestHelper helper) {
        SolarAssertions.setClear(helper);
        SolarAssertions.setDayTime(helper, DAWN);
        placePanel(helper, PANEL, Direction.EAST, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacity(helper, PANEL, expectedPeak(),
                        "East-facing panel at dawn"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_dusk")
    public static void westPeaksAtDusk(GameTestHelper helper) {
        SolarAssertions.setClear(helper);
        SolarAssertions.setDayTime(helper, DUSK);
        placePanel(helper, PANEL, Direction.WEST, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacity(helper, PANEL, expectedPeak(),
                        "West-facing panel at dusk"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void northStaysAtTheFloor(GameTestHelper helper) {
        SolarAssertions.setClear(helper);
        SolarAssertions.setDayTime(helper, DAWN);
        placePanel(helper, PANEL, Direction.NORTH, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacity(helper, PANEL, HorizontalSolarOutput.MIN_CAPACITY,
                        "A north-facing panel never tracks the sun"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ variants

    @GameTest(template = PLATFORM, batch = "horiz_dawn")
    public static void advancedOutproducesBasic(GameTestHelper helper) {
        SolarAssertions.setClear(helper);
        SolarAssertions.setDayTime(helper, DAWN);
        placePanel(helper, PANEL, Direction.EAST, advanced());
        placePanel(helper, REFERENCE, Direction.EAST, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertFasterThan(helper, PANEL, REFERENCE,
                        "The advanced horizontal generator should beat the basic one"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ SU stability

    /**
     * The regression from an earlier version: stress capacity accumulating instead
     * of staying put. A full server restart cannot be driven from a game test, but
     * the day/night cycle can, and that is where the accumulation showed.
     *
     * <p>
     * See {@code docs/test-world.md} for the manual restart checks that go with
     * this.
     */
    @GameTest(template = PLATFORM, timeoutTicks = 400, batch = "horiz_cycle")
    public static void capacitySurvivesDayNightCycles(GameTestHelper helper) {
        SolarAssertions.setClear(helper);
        SolarAssertions.setDayTime(helper, DAWN);
        placePanel(helper, PANEL, Direction.EAST, basic());

        // One linear sequence. Nesting a second startSequence inside a thenExecute
        // would let the outer one succeed before the inner checks ever ran.
        float[] baseline = new float[1];

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> baseline[0] = SolarAssertions.stressCapacityAt(helper, PANEL))
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, DAWN))
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacity(helper, PANEL, baseline[0],
                        "Capacity after one night/day cycle must match the value before it"))
                // A second cycle: the reported bug grew the value on every load, so one
                // round trip could look fine by accident.
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, DAWN))
                .thenIdle(SETTLE)
                .thenExecute(() -> assertCapacity(helper, PANEL, baseline[0],
                        "Capacity after two night/day cycles must still match -- if it has grown, "
                                + "the stress capacity is accumulating instead of being replaced"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ helpers

    /** The peak capacity the curve should reach with the configured base. */
    private static float expectedPeak() {
        return HorizontalSolarOutput.stressCapacity(
                com.createphotomovement.logic.SolarFacing.EAST, DAWN, baseCapacity(), false);
    }

    private static int baseCapacity() {
        return com.createphotomovement.infrastructure.config.PMConfigs.server().stressCapacity.get();
    }

    private static void placePanel(GameTestHelper helper, BlockPos pos, Direction facing, Block block) {
        BlockState state = block.defaultBlockState()
                .setValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING, facing);
        helper.setBlock(pos, state);
    }

    private static void assertCapacity(GameTestHelper helper, BlockPos pos, float expected, String why) {
        float actual = SolarAssertions.stressCapacityAt(helper, pos);
        if (Math.abs(actual - expected) > 0.01f)
            throw new net.minecraft.gametest.framework.GameTestAssertException(
                    why + ": expected " + expected + " SU/RPM but got " + actual + front(helper, pos));
    }

    private static void assertCapacityAbove(GameTestHelper helper, BlockPos pos, float floor, String why) {
        float actual = SolarAssertions.stressCapacityAt(helper, pos);
        if (actual <= floor)
            throw new net.minecraft.gametest.framework.GameTestAssertException(
                    why + ": expected more than " + floor + " SU/RPM but got " + actual + front(helper, pos));
    }

    /** What the panel is actually looking at, read back off the placed block state. */
    private static String front(GameTestHelper helper, BlockPos pos) {
        BlockState placed = helper.getBlockState(pos);
        if (!placed.hasProperty(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING))
            return " [placed block " + placed.getBlock().getName().getString()
                    + " has no HORIZONTAL_FACING property]";
        return SolarAssertions.describeFront(helper, pos,
                placed.getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING));
    }

    private static Block basic() {
        return AllBlocks.HORIZONTAL_SOLAR_GENERATOR.get();
    }

    private static Block advanced() {
        return AllBlocks.HORZ_ADV_SOLAR_GENERATOR.get();
    }
}
