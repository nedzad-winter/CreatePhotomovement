package com.createphotomovement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Speed of the vertical solar generators.
 *
 * <p>
 * Note what is <em>not</em> covered here: whether the generator runs at all at
 * night, under a carpet or under glass depends on sky light and block states,
 * which only exist in a running world. Those cases are the job of the game
 * tests. This class is reached only once the block entity has already decided
 * that generation is possible.
 */
class SolarGeneratorOutputTest {

    /** The default from PMServer. */
    private static final int CONFIGURED_SPEED = 16;

    @Test
    @DisplayName("basic generator runs at the configured speed on a clear day")
    void basicClear() {
        assertEquals(16f,
                SolarGeneratorOutput.generatedSpeed(CONFIGURED_SPEED, SolarGeneratorOutput.BASIC_MULTIPLIER, false),
                1e-6);
    }

    @Test
    @DisplayName("rain halves the basic generator")
    void basicRain() {
        assertEquals(8f,
                SolarGeneratorOutput.generatedSpeed(CONFIGURED_SPEED, SolarGeneratorOutput.BASIC_MULTIPLIER, true),
                1e-6);
    }

    @Test
    @DisplayName("advanced generator doubles the basic one under the same conditions")
    void advancedDoublesBasic() {
        float basic = SolarGeneratorOutput.generatedSpeed(CONFIGURED_SPEED, SolarGeneratorOutput.BASIC_MULTIPLIER,
                false);
        float advanced = SolarGeneratorOutput.generatedSpeed(CONFIGURED_SPEED,
                SolarGeneratorOutput.ADVANCED_MULTIPLIER, false);

        assertEquals(32f, advanced, 1e-6);
        assertEquals(2f * basic, advanced, 1e-6);
    }

    @Test
    @DisplayName("the advanced generator is halved by rain just like the basic one")
    void advancedRain() {
        float basicRain = SolarGeneratorOutput.generatedSpeed(CONFIGURED_SPEED,
                SolarGeneratorOutput.BASIC_MULTIPLIER, true);
        float advancedRain = SolarGeneratorOutput.generatedSpeed(CONFIGURED_SPEED,
                SolarGeneratorOutput.ADVANCED_MULTIPLIER, true);

        assertEquals(16f, advancedRain, 1e-6);
        assertEquals(2f * basicRain, advancedRain, 1e-6);
    }

    @Test
    @DisplayName("the rain penalty is applied after the variant multiplier, not before")
    void rainAppliesAfterMultiplier() {
        // Both orderings happen to agree for a factor of two, so pin the order with a
        // configured speed that would round differently if it were applied first.
        assertEquals(2.5f,
                SolarGeneratorOutput.generatedSpeed(5, SolarGeneratorOutput.BASIC_MULTIPLIER, true), 1e-6);
        assertEquals(5f,
                SolarGeneratorOutput.generatedSpeed(5, SolarGeneratorOutput.ADVANCED_MULTIPLIER, true), 1e-6);
    }

    @Test
    @DisplayName("a configured speed of zero produces nothing, rain or shine")
    void zeroConfiguredSpeed() {
        assertEquals(0f, SolarGeneratorOutput.generatedSpeed(0, SolarGeneratorOutput.BASIC_MULTIPLIER, false), 1e-6);
        assertEquals(0f, SolarGeneratorOutput.generatedSpeed(0, SolarGeneratorOutput.ADVANCED_MULTIPLIER, true), 1e-6);
    }

    @Test
    @DisplayName("the sky light threshold matches the README")
    void skyLightThreshold() {
        assertEquals(12, SolarGeneratorOutput.MIN_SKY_LIGHT);
    }
}
