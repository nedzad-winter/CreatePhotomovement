package com.createphotomovement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WindmillOutputTest {

    /** Create's default windmillSailsPerRPM. */
    private static final int BRACKET = 8;

    private static final int FORWARD = 1;
    private static final int REVERSE = -1;

    @Nested
    @DisplayName("speed")
    class Speed {

        @ParameterizedTest(name = "{0} sails -> {1} RPM")
        @CsvSource({
                "0,   1",
                "7,   1",
                "8,   1",
                "16,  2",
                "32,  4",
                "64,  8",
                "128, 16"
        })
        void followsTheSailBrackets(int sails, float expectedRpm) {
            assertEquals(expectedRpm, WindmillOutput.generatedSpeed(sails, 0, BRACKET, FORWARD), 1e-6);
        }

        @Test
        @DisplayName("a bearing with no sails still turns at the minimum")
        void zeroSailsStillTurns() {
            assertEquals(WindmillOutput.MIN_RPM, WindmillOutput.generatedSpeed(0, 0, BRACKET, FORWARD), 1e-6);
        }

        @Test
        @DisplayName("speed caps at 16 RPM no matter how many sails")
        void capsAtMaxRpm() {
            assertEquals(WindmillOutput.MAX_RPM, WindmillOutput.generatedSpeed(128, 0, BRACKET, FORWARD), 1e-6);
            assertEquals(WindmillOutput.MAX_RPM, WindmillOutput.generatedSpeed(1000, 0, BRACKET, FORWARD), 1e-6);
            assertEquals(WindmillOutput.MAX_RPM, WindmillOutput.generatedSpeed(500, 500, BRACKET, FORWARD), 1e-6);
        }

        @Test
        @DisplayName("solar sails count towards speed exactly like regular ones")
        void solarSailsDoNotBoostSpeed() {
            assertEquals(WindmillOutput.generatedSpeed(16, 0, BRACKET, FORWARD),
                    WindmillOutput.generatedSpeed(0, 16, BRACKET, FORWARD), 1e-6);
            assertEquals(WindmillOutput.generatedSpeed(16, 0, BRACKET, FORWARD),
                    WindmillOutput.generatedSpeed(8, 8, BRACKET, FORWARD), 1e-6);
        }

        @Test
        @DisplayName("reversing the bearing negates the speed but not its magnitude")
        void negativeDirection() {
            assertEquals(-4f, WindmillOutput.generatedSpeed(32, 0, BRACKET, REVERSE), 1e-6);
            assertEquals(Math.abs(WindmillOutput.generatedSpeed(32, 0, BRACKET, FORWARD)),
                    Math.abs(WindmillOutput.generatedSpeed(32, 0, BRACKET, REVERSE)), 1e-6);
        }

        @Test
        @DisplayName("even a reversed bearing with no sails turns at the minimum")
        void zeroSailsReversed() {
            assertEquals(-WindmillOutput.MIN_RPM, WindmillOutput.generatedSpeed(0, 0, BRACKET, REVERSE), 1e-6);
        }
    }

    @Nested
    @DisplayName("solar multiplier")
    class Multiplier {

        private static final long NOON = 6000L;
        private static final long MIDNIGHT = 18000L;

        @Test
        void fullBonusOnAClearDayWithSkyAccess() {
            assertEquals(WindmillOutput.MULTIPLIER_CLEAR,
                    WindmillOutput.solarMultiplier(true, NOON, Weather.CLEAR), 1e-6);
        }

        @Test
        void reducedBonusInRain() {
            assertEquals(WindmillOutput.MULTIPLIER_RAIN,
                    WindmillOutput.solarMultiplier(true, NOON, Weather.RAIN), 1e-6);
        }

        @Test
        void noBonusInAThunderstorm() {
            assertEquals(WindmillOutput.MULTIPLIER_NONE,
                    WindmillOutput.solarMultiplier(true, NOON, Weather.THUNDER), 1e-6);
        }

        @Test
        void noBonusAtNight() {
            assertEquals(WindmillOutput.MULTIPLIER_NONE,
                    WindmillOutput.solarMultiplier(true, MIDNIGHT, Weather.CLEAR), 1e-6);
        }

        @Test
        @DisplayName("no sky access beats everything, even a clear midday sky")
        void skyAccessIsCheckedFirst() {
            for (Weather weather : Weather.values()) {
                assertEquals(WindmillOutput.MULTIPLIER_NONE,
                        WindmillOutput.solarMultiplier(false, NOON, weather), 1e-6,
                        "covered array got a bonus in " + weather);
            }
        }

        @Test
        @DisplayName("the bonus switches off exactly at nightfall")
        void bonusEndsAtNightfall() {
            assertEquals(WindmillOutput.MULTIPLIER_CLEAR,
                    WindmillOutput.solarMultiplier(true, DayCycle.NIGHT_START - 1, Weather.CLEAR), 1e-6);
            assertEquals(WindmillOutput.MULTIPLIER_NONE,
                    WindmillOutput.solarMultiplier(true, DayCycle.NIGHT_START, Weather.CLEAR), 1e-6);
        }

        @Test
        @DisplayName("the bonus comes back exactly at dawn")
        void bonusReturnsAtDawn() {
            assertEquals(WindmillOutput.MULTIPLIER_NONE,
                    WindmillOutput.solarMultiplier(true, DayCycle.NIGHT_END - 1, Weather.CLEAR), 1e-6);
            assertEquals(WindmillOutput.MULTIPLIER_CLEAR,
                    WindmillOutput.solarMultiplier(true, DayCycle.NIGHT_END, Weather.CLEAR), 1e-6);
        }
    }

    @Nested
    @DisplayName("stress capacity")
    class Capacity {

        /** Total SU as the player sees it: capacity per RPM multiplied by the speed. */
        private float totalSu(int regular, int solar, float multiplier) {
            float perRpm = WindmillOutput.stressCapacityPerRpm(regular, solar, BRACKET, multiplier);
            float speed = Math.abs(WindmillOutput.generatedSpeed(regular, solar, BRACKET, FORWARD));
            return perRpm * speed;
        }

        @ParameterizedTest(name = "{0} regular sails -> {1} SU")
        @CsvSource({
                "8,   512",
                "16,  1024",
                "32,  2048",
                "64,  4096",
                "128, 8192"
        })
        @DisplayName("regular sails reproduce the standard column of the README table")
        void standardColumn(int sails, float expectedSu) {
            assertEquals(expectedSu, totalSu(sails, 0, WindmillOutput.MULTIPLIER_NONE), 1e-3);
        }

        @ParameterizedTest(name = "{0} solar sails in rain -> {1} SU")
        @CsvSource({
                "8,   768",
                "16,  1536",
                "32,  3072",
                "64,  6144",
                "128, 12288"
        })
        @DisplayName("solar sails in rain reproduce the rain column of the README table")
        void rainColumn(int sails, float expectedSu) {
            assertEquals(expectedSu, totalSu(0, sails, WindmillOutput.MULTIPLIER_RAIN), 1e-3);
        }

        @ParameterizedTest(name = "{0} solar sails in sun -> {1} SU")
        @CsvSource({
                "8,   1024",
                "16,  2048",
                "32,  4096",
                "64,  8192",
                "128, 16384"
        })
        @DisplayName("solar sails in sun reproduce the sunny column of the README table")
        void sunnyColumn(int sails, float expectedSu) {
            assertEquals(expectedSu, totalSu(0, sails, WindmillOutput.MULTIPLIER_CLEAR), 1e-3);
        }

        @Test
        @DisplayName("no sails produce no stress capacity")
        void zeroSails() {
            assertEquals(0f, WindmillOutput.stressCapacityPerRpm(0, 0, BRACKET, WindmillOutput.MULTIPLIER_CLEAR),
                    1e-6);
        }

        @Test
        @DisplayName("a partial bracket produces nothing")
        void partialBracketIsWorthNothing() {
            // Seven sails do not fill a bracket, so the bearing spins at the minimum
            // speed while contributing no stress capacity at all.
            assertEquals(0f, WindmillOutput.stressCapacityPerRpm(7, 0, BRACKET, WindmillOutput.MULTIPLIER_CLEAR),
                    1e-6);
            assertEquals(WindmillOutput.MIN_RPM, WindmillOutput.generatedSpeed(7, 0, BRACKET, FORWARD), 1e-6);
        }

        @Test
        @DisplayName("sails beyond a bracket boundary are ignored until the next one fills")
        void bracketsAreWholeOnly() {
            assertEquals(totalSu(8, 0, WindmillOutput.MULTIPLIER_NONE),
                    totalSu(15, 0, WindmillOutput.MULTIPLIER_NONE), 1e-3);
        }

        @Test
        @DisplayName("mixed sails add up: regular at face value, solar scaled")
        void mixedSails() {
            // 8 regular -> 512, 8 solar at 2.0 -> 1024, total 1536.
            assertEquals(1536f, totalSu(8, 8, WindmillOutput.MULTIPLIER_CLEAR), 1e-3);
        }

        @Test
        @DisplayName("solar sails without sky access are worth exactly as much as regular ones")
        void coveredSolarSailsMatchRegular() {
            assertEquals(totalSu(16, 0, WindmillOutput.MULTIPLIER_NONE),
                    totalSu(0, 16, WindmillOutput.MULTIPLIER_NONE), 1e-3);
        }

        @Test
        @DisplayName("capacity per RPM is a flat 512 for regular sails at any count")
        void perRpmIsFlatForRegularSails() {
            // The bracket count cancels out: brackets*512 / brackets.
            for (int sails = 8; sails <= 512; sails += 8) {
                assertEquals(WindmillOutput.SU_PER_BRACKET,
                        WindmillOutput.stressCapacityPerRpm(sails, 0, BRACKET, WindmillOutput.MULTIPLIER_NONE), 1e-3,
                        "per-RPM capacity moved at " + sails + " sails");
            }
        }

        @Test
        @DisplayName("total SU saturates once the speed cap is reached")
        void totalSuSaturatesAtTheSpeedCap() {
            // Since per-RPM capacity is flat, the total only grows while the speed does.
            // At 128 sails the bearing hits MAX_RPM, so further sails add nothing.
            float at128 = totalSu(128, 0, WindmillOutput.MULTIPLIER_NONE);
            float at256 = totalSu(256, 0, WindmillOutput.MULTIPLIER_NONE);
            float at1024 = totalSu(1024, 0, WindmillOutput.MULTIPLIER_NONE);

            assertEquals(8192f, at128, 1e-3);
            assertEquals(at128, at256, 1e-3);
            assertEquals(at128, at1024, 1e-3);
        }

        @Test
        @DisplayName("below the cap, doubling the sails doubles the total SU")
        void totalSuIsLinearBelowTheCap() {
            assertEquals(2f * totalSu(32, 0, WindmillOutput.MULTIPLIER_NONE),
                    totalSu(64, 0, WindmillOutput.MULTIPLIER_NONE), 1e-3);
        }

        @Test
        @DisplayName("a sail bracket of zero is rejected rather than dividing by zero")
        void rejectsZeroBracket() {
            assertThrows(IllegalArgumentException.class,
                    () -> WindmillOutput.stressCapacityPerRpm(8, 0, 0, WindmillOutput.MULTIPLIER_CLEAR));
            assertThrows(IllegalArgumentException.class,
                    () -> WindmillOutput.generatedSpeed(8, 0, 0, FORWARD));
        }
    }
}
