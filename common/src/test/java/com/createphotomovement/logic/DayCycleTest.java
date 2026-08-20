package com.createphotomovement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DayCycleTest {

    @Nested
    @DisplayName("timeOfDay")
    class TimeOfDay {

        @Test
        void wrapsAtTheEndOfTheDay() {
            assertEquals(0L, DayCycle.timeOfDay(DayCycle.DAY_LENGTH));
            assertEquals(1000L, DayCycle.timeOfDay(DayCycle.DAY_LENGTH + 1000L));
        }

        @Test
        void handlesManyDaysOfUptime() {
            // A long-running world reaches large day times; the modulo must still hold.
            assertEquals(6000L, DayCycle.timeOfDay(500L * DayCycle.DAY_LENGTH + 6000L));
        }

        @Test
        void negativeTimesWrapForwardsNotBackwards() {
            // Java's % keeps the sign of the dividend, which would produce a negative
            // ratio further down the line. timeOfDay has to correct for that.
            assertEquals(23000L, DayCycle.timeOfDay(-1000L));
            assertEquals(0L, DayCycle.timeOfDay(-DayCycle.DAY_LENGTH));
        }
    }

    @Nested
    @DisplayName("isNight")
    class IsNight {

        @ParameterizedTest(name = "day time {0} -> night={1}")
        @CsvSource({
                "0,     false",
                "6000,  false",
                "12999, false",
                "13000, true",
                "18000, true",
                "22999, true",
                "23000, false",
                "23999, false"
        })
        void bracketsTheNightExactly(long dayTime, boolean expected) {
            assertEquals(expected, DayCycle.isNight(dayTime));
        }

        @Test
        void nightBoundariesAreHalfOpen() {
            // The exact tick night starts counts as night; the tick it ends does not.
            assertTrue(DayCycle.isNight(DayCycle.NIGHT_START));
            assertFalse(DayCycle.isNight(DayCycle.NIGHT_END));
        }

        @Test
        void appliesToTheSecondDayToo() {
            assertTrue(DayCycle.isNight(DayCycle.DAY_LENGTH + 13000L));
            assertFalse(DayCycle.isNight(DayCycle.DAY_LENGTH + 6000L));
        }
    }

    @Nested
    @DisplayName("daylightRatio")
    class DaylightRatio {

        @Test
        void runsFromZeroAtDawnToOneAtDusk() {
            assertEquals(0f, DayCycle.daylightRatio(0), 1e-6);
            assertEquals(0.5f, DayCycle.daylightRatio(6000), 1e-6);
            assertEquals(1f, DayCycle.daylightRatio(DayCycle.DAYLIGHT_END), 1e-6);
        }

        @Test
        void staysAtOneThroughTheNight() {
            // Everything past dusk clamps. Whether the generator produces anything at
            // night is decided by sky light in the block entity, not here.
            assertEquals(1f, DayCycle.daylightRatio(18000), 1e-6);
            assertEquals(1f, DayCycle.daylightRatio(23999), 1e-6);
        }

        @Test
        void neverLeavesTheUnitInterval() {
            for (long t = -DayCycle.DAY_LENGTH; t <= 3 * DayCycle.DAY_LENGTH; t += 137) {
                float ratio = DayCycle.daylightRatio(t);
                assertTrue(ratio >= 0f && ratio <= 1f, "ratio out of range at dayTime " + t + ": " + ratio);
            }
        }
    }

    @Nested
    @DisplayName("clamp")
    class Clamp {

        @Test
        void clampsFloats() {
            assertEquals(0f, DayCycle.clamp(-5f, 0f, 1f), 1e-6);
            assertEquals(1f, DayCycle.clamp(5f, 0f, 1f), 1e-6);
            assertEquals(0.5f, DayCycle.clamp(0.5f, 0f, 1f), 1e-6);
        }

        @Test
        void clampsInts() {
            assertEquals(1, DayCycle.clamp(0, 1, 16));
            assertEquals(16, DayCycle.clamp(99, 1, 16));
            assertEquals(7, DayCycle.clamp(7, 1, 16));
        }
    }
}
