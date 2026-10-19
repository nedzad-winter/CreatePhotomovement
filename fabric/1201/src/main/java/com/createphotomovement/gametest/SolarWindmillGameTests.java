package com.createphotomovement.gametest;

import com.createphotomovement.AllBlocks;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;

/**
 * In-game tests for the solar windmill bearing on Fabric 1.20.1.
 *
 * <p>
 * Mirrors the scenario of the same name in the NeoForge classes. Only the
 * mixed-sail scenario has been ported so far; the rest of the windmill tests are
 * still open, see {@code docs/gametests.md}. This one is here first because it
 * guards a fix that applies to all three targets.
 *
 * <p>
 * Fabric wants instance methods and the {@link FabricGameTest} interface, and
 * brings its own empty structure instead of the {@code solar_platform} template
 * the NeoForge targets use -- the windmills only need open air around them.
 */
public class SolarWindmillGameTests implements FabricGameTest {

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
    @GameTest(template = EMPTY_STRUCTURE, batch = "windmill_day")
    public void regularSailsNextToSolarSailsCount(GameTestHelper helper) {
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
        return AllBlocks.SOLAR_WINDMILL_BEARING;
    }

    private static Block solarSail() {
        return AllBlocks.SOLAR_SAIL;
    }

    /** Create's own sail frame; it carries the {@code windmill_sails} tag. */
    private static Block plainSail() {
        return com.simibubi.create.AllBlocks.SAIL_FRAME.get();
    }
}
