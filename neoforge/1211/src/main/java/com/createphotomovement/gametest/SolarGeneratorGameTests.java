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
 * In-game tests for the vertical solar generators on NeoForge 1.21.1.
 *
 * <p>
 * Deliberately thin: every check lives in {@link SolarAssertions} in
 * {@code common/}, because the same scenarios have to run on all three targets
 * and only this scaffolding differs between them. NeoForge wants static methods
 * with its own annotations; Fabric wants instance methods with different ones.
 *
 * <p>
 * All tests share one structure template, {@code solar_platform} -- a flat
 * floor with open sky. See {@code docs/gametests.md} for how to build it.
 */
@GameTestHolder(CreatePhotomovement.MOD_ID)
@PrefixGameTestTemplate(false)
public class SolarGeneratorGameTests {

    private static final String PLATFORM = "solar_platform";

    /** Where the generator under test goes. Sits on the floor with sky above. */
    private static final BlockPos GENERATOR = new BlockPos(2, 1, 2);

    /** A second slot, for tests that compare two generators side by side. */
    private static final BlockPos REFERENCE = new BlockPos(5, 1, 2);

    /** Directly above {@link #GENERATOR}: the block that can shade the panel. */
    private static final BlockPos COVER = new BlockPos(2, 2, 2);

    /** Ticks to let the light engine settle after placing a block. */
    private static final int SETTLE = 5;

    // ------------------------------------------------------------ sky access

    @GameTest(template = PLATFORM)
    public static void generatesUnderOpenSky(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    // Checked first and explicitly: if the template has a ceiling, every
                    // other test in this class fails for a reason that looks like a bug
                    // in the mod.
                    SolarAssertions.assertHasSkyAccess(helper, COVER);
                    SolarAssertions.assertGenerating(helper, GENERATOR, "Open sky at midday");
                })
                .thenSucceed();
    }

    @GameTest(template = PLATFORM)
    public static void stopsUnderStone(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());
        helper.setBlock(COVER, Blocks.STONE);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertIdle(helper, GENERATOR, "Solid stone overhead"))
                .thenSucceed();
    }

    /**
     * The case where the loaders used to disagree. NeoForge additionally checked
     * {@code getLightBlock() > 0} and Fabric did not; the decision was to keep the
     * lenient behaviour, so glass must NOT stop generation.
     */
    @GameTest(template = PLATFORM)
    public static void keepsGeneratingUnderGlass(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());
        helper.setBlock(COVER, Blocks.GLASS);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertGenerating(helper, GENERATOR,
                        "Glass overhead should not block generation"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM)
    public static void stopsUnderCarpet(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());
        helper.setBlock(COVER, Blocks.WHITE_CARPET);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertIdle(helper, GENERATOR, "Carpet on the panel"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM)
    public static void stopsUnderSnowLayer(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());
        helper.setBlock(COVER, Blocks.SNOW);

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertIdle(helper, GENERATOR, "Snow layer on the panel"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ time and weather

    @GameTest(template = PLATFORM)
    public static void idleAtNight(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    SolarAssertions.assertIsNight(helper);
                    SolarAssertions.assertIdle(helper, GENERATOR, "Midnight");
                })
                .thenSucceed();
    }

    @GameTest(template = PLATFORM)
    public static void rainHalvesTheOutput(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());

        // One linear sequence. Nesting a second startSequence inside a thenExecute
        // would let the outer one succeed before the inner check ever ran.
        float[] whenClear = new float[1];

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> whenClear[0] = Math.abs(SolarAssertions.generatedSpeedAt(helper, GENERATOR)))
                .thenExecute(() -> SolarAssertions.setRaining(helper))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertSpeed(helper, GENERATOR, whenClear[0] / 2f,
                        "Rain should halve the output"))
                .thenSucceed();
    }

    /**
     * The regression from an earlier version: stress capacity accumulating across
     * loads instead of staying put. A server restart cannot be driven from a game
     * test; the day/night cycle can, and that is where it showed. The manual restart
     * checks are in {@code docs/test-world.md}.
     */
    @GameTest(template = PLATFORM, timeoutTicks = 400)
    public static void capacitySurvivesDayNightCycles(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, basic());

        float[] baseline = new float[1];

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> baseline[0] = SolarAssertions.stressCapacityAt(helper, GENERATOR))
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertCapacity(helper, GENERATOR, baseline[0],
                        "Capacity after one night/day cycle"))
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertCapacity(helper, GENERATOR, baseline[0],
                        "Capacity after two night/day cycles -- if it has grown, the stress capacity "
                                + "is accumulating instead of being replaced"))
                .thenSucceed();
    }

    @GameTest(template = PLATFORM, timeoutTicks = 400)
    public static void advancedCapacitySurvivesDayNightCycles(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, advanced());

        float[] baseline = new float[1];

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> baseline[0] = SolarAssertions.stressCapacityAt(helper, GENERATOR))
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDNIGHT))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY))
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertCapacity(helper, GENERATOR, baseline[0],
                        "Advanced generator capacity after a night/day cycle"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ variants

    @GameTest(template = PLATFORM)
    public static void advancedOutproducesBasic(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, advanced());
        helper.setBlock(REFERENCE, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertFasterThan(helper, GENERATOR, REFERENCE,
                        "The advanced generator should beat the basic one"))
                .thenSucceed();
    }

    /**
     * Guards the AllBlocks/DyeColor refactor planned for a later branch: a dyed
     * variant must behave exactly like the undyed one. If that refactor ever
     * changes behaviour per colour, this fails.
     */
    @GameTest(template = PLATFORM)
    public static void dyedVariantMatchesUndyed(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        helper.setBlock(GENERATOR, AllBlocks.BLUE_SOLAR_GENERATOR.get());
        helper.setBlock(REFERENCE, basic());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> SolarAssertions.assertSameSpeed(helper, GENERATOR, REFERENCE,
                        "A dyed generator must behave like the undyed one"))
                .thenSucceed();
    }

    // ------------------------------------------------------------ helpers

    private static Block basic() {
        return AllBlocks.SOLAR_GENERATOR.get();
    }

    private static Block advanced() {
        return AllBlocks.ADV_SOLAR_GENERATOR.get();
    }
}
