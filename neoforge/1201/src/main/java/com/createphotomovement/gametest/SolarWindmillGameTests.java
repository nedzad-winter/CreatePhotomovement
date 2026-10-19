package com.createphotomovement.gametest;

import com.createphotomovement.AllBlocks;
import com.createphotomovement.CreatePhotomovement;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * In-game tests for the solar windmill bearing on NeoForge 1.20.1.
 *
 * <p>
 * Mirrors the scenarios of the same name in the NeoForge 1.21.1 class. Only the
 * mixed-sail scenario has been ported so far; the rest of the windmill tests are
 * still open, see {@code docs/gametests.md}. This one is here first because it
 * guards a fix that applies to all three targets.
 */
@GameTestHolder(CreatePhotomovement.MOD_ID)
@PrefixGameTestTemplate(false)
public class SolarWindmillGameTests {

    private static final String PLATFORM = "solar_platform";

    private static final BlockPos BEARING = new BlockPos(3, 4, 4);

    /** An identical bearing carrying plain Create sails, to compare against. */
    private static final BlockPos REFERENCE = new BlockPos(10, 4, 4);

    /** Ticks to let the bearing finish its warmup and settle. */
    private static final int SETTLE = 15;

    /**
     * A solar windmill extended with a few of Create's own sails.
     *
     * <p>
     * Five solar and four regular sails make nine, one bracket, but each kind alone
     * is short of one. Rounding them down separately gave no capacity at all and
     * the regular sails never counted. The capacity is judged against a plain
     * reference rather than a recomputed figure, so the test fails against the old
     * arithmetic instead of agreeing with it.
     */
    @GameTest(template = PLATFORM, batch = "windmill_day")
    public static void regularSailsNextToSolarSailsCount(GameTestHelper helper) {
        SolarAssertions.setDayTime(helper, SolarAssertions.MIDDAY);
        SolarAssertions.setClear(helper);
        int regular = 4;
        int solar = WindmillAssertions.SAIL_COUNT - regular;
        WindmillAssertions.buildWindmill(helper, BEARING, bearing(), solarSail(), plainSail(), regular);
        WindmillAssertions.buildWindmill(helper, REFERENCE, bearing(), plainSail());

        helper.startSequence()
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assemble(helper, BEARING);
                    WindmillAssertions.assemble(helper, REFERENCE);
                })
                .thenIdle(SETTLE)
                .thenExecute(() -> {
                    WindmillAssertions.assertRunning(helper, BEARING, "Solar and regular sails on one bearing");
                    WindmillAssertions.assertSailCounts(helper, BEARING, regular, solar,
                            "Create's own sails must be counted as regular sails next to solar ones");
                    WindmillAssertions.assertBetweenPlainAndSolar(helper, BEARING, REFERENCE,
                            WindmillAssertions.BONUS_CLEAR,
                            "Five solar and four regular sails fill one bracket together");
                })
                .thenSucceed();
    }

    private static Block bearing() {
        return AllBlocks.SOLAR_WINDMILL_BEARING.get();
    }

    private static Block solarSail() {
        return AllBlocks.SOLAR_SAIL.get();
    }

    /** Create's own sail frame; it carries the {@code windmill_sails} tag. */
    private static Block plainSail() {
        return com.simibubi.create.AllBlocks.SAIL_FRAME.get();
    }
}
