package com.createphotomovement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class HorizontalSolarOutputTest {

    /** The default from PMServer. */
    private static final int BASE = 16;

    /** With the default base, the curve tops out here. */
    private static final float PEAK = HorizontalSolarOutput.PEAK_MULTIPLIER * BASE;

    private static float capacity(SolarFacing facing, long dayTime) {
        return HorizontalSolarOutput.stressCapacity(facing, dayTime, BASE, false);
    }

    @Nested
    @DisplayName("east-facing panel")
    class East {

        @Test
        void peaksAtDawn() {
            assertEquals(PEAK, capacity(SolarFacing.EAST, 0), 1e-6);
        }

        @Test
        void bottomsOutAtDusk() {
            assertEquals(HorizontalSolarOutput.MIN_CAPACITY, capacity(SolarFacing.EAST, DayCycle.DAYLIGHT_END), 1e-6);
        }

        @Test
        void fallsOffQuadraticallyThroughTheDay() {
            // ratio 0.5 -> factor 0.25 -> 8 + (64-8)*0.25 = 22
            assertEquals(22f, capacity(SolarFacing.EAST, 6000), 1e-6);
        }

        @Test
        void decreasesMonotonicallyUntilDusk() {
            float previous = Float.MAX_VALUE;
            for (long t = 0; t <= DayCycle.DAYLIGHT_END; t += 250) {
                float current = capacity(SolarFacing.EAST, t);
                assertTrue(current <= previous, "east output rose at dayTime " + t);
                previous = current;
            }
        }
    }

    @Nested
    @DisplayName("west-facing panel")
    class West {

        @Test
        void bottomsOutAtDawn() {
            assertEquals(HorizontalSolarOutput.MIN_CAPACITY, capacity(SolarFacing.WEST, 0), 1e-6);
        }

        @Test
        void peaksAtDusk() {
            assertEquals(PEAK, capacity(SolarFacing.WEST, DayCycle.DAYLIGHT_END), 1e-6);
        }

        @Test
        void risesQuadraticallyThroughTheDay() {
            assertEquals(22f, capacity(SolarFacing.WEST, 6000), 1e-6);
        }

        @Test
        void increasesMonotonicallyUntilDusk() {
            float previous = Float.MIN_VALUE;
            for (long t = 0; t <= DayCycle.DAYLIGHT_END; t += 250) {
                float current = capacity(SolarFacing.WEST, t);
                assertTrue(current >= previous, "west output fell at dayTime " + t);
                previous = current;
            }
        }
    }

    @Test
    @DisplayName("east and west are mirror images at every point of the day")
    void eastAndWestAreMirrored() {
        for (long t = 0; t <= DayCycle.DAYLIGHT_END; t += 500) {
            assertEquals(capacity(SolarFacing.EAST, t), capacity(SolarFacing.WEST, DayCycle.DAYLIGHT_END - t), 1e-6,
                    "mirror broken at dayTime " + t);
        }
    }

    @Test
    @DisplayName("east and west cross at midday")
    void crossAtMidday() {
        assertEquals(capacity(SolarFacing.EAST, 6000), capacity(SolarFacing.WEST, 6000), 1e-6);
    }

    @ParameterizedTest(name = "{0} sits at the minimum all day")
    @CsvSource({ "0", "3000", "6000", "9000", "12000", "18000" })
    @DisplayName("north and south never track the sun")
    void otherFacingsStayAtMinimum(long dayTime) {
        assertEquals(HorizontalSolarOutput.MIN_CAPACITY, capacity(SolarFacing.OTHER, dayTime), 1e-6);
    }

    @ParameterizedTest
    @EnumSource(SolarFacing.class)
    @DisplayName("an obstruction pins the output to the minimum for every facing and time")
    void obstructionOverridesEverything(SolarFacing facing) {
        for (long t = 0; t <= DayCycle.DAY_LENGTH; t += 1500) {
            assertEquals(HorizontalSolarOutput.MIN_CAPACITY,
                    HorizontalSolarOutput.stressCapacity(facing, t, BASE, true), 1e-6,
                    "obstructed output moved at dayTime " + t);
        }
    }

    @Test
    @DisplayName("after dusk the curve freezes rather than reversing")
    void nightFreezesTheCurve() {
        // daylightRatio clamps at 1, so a west-facing panel still reports its peak all
        // night. That is intentional: the block entity gates on sky light before this
        // value is ever used. Pinned here so the behaviour cannot change silently.
        assertEquals(PEAK, capacity(SolarFacing.WEST, 18000), 1e-6);
        assertEquals(HorizontalSolarOutput.MIN_CAPACITY, capacity(SolarFacing.EAST, 18000), 1e-6);
    }

    @Test
    @DisplayName("output never drops below the minimum")
    void neverBelowMinimum() {
        for (SolarFacing facing : SolarFacing.values()) {
            for (long t = 0; t <= DayCycle.DAY_LENGTH; t += 250) {
                assertTrue(capacity(facing, t) >= HorizontalSolarOutput.MIN_CAPACITY,
                        "below minimum for " + facing + " at dayTime " + t);
            }
        }
    }

    @Test
    @DisplayName("results are whole numbers")
    void alwaysRounded() {
        for (long t = 0; t <= DayCycle.DAYLIGHT_END; t += 173) {
            float value = capacity(SolarFacing.EAST, t);
            assertEquals(Math.round(value), value, 1e-6, "not rounded at dayTime " + t);
        }
    }

    @Test
    @DisplayName("the peak scales with the configured base capacity")
    void peakFollowsConfig() {
        assertEquals(4 * 32f, HorizontalSolarOutput.stressCapacity(SolarFacing.EAST, 0, 32, false), 1e-6);
        assertEquals(4 * 64f, HorizontalSolarOutput.stressCapacity(SolarFacing.EAST, 0, 64, false), 1e-6);
    }

    @Test
    @DisplayName("a base capacity below the floor makes the curve run downwards")
    void tinyBaseInvertsTheCurve() {
        // base 1 -> peak 4, which is below MIN_CAPACITY of 8, so the interpolation runs
        // from 8 down to 4 instead of up. Nothing clamps that. Pinned deliberately: if
        // a floor at MIN_CAPACITY is ever wanted, this test should fail and be updated
        // rather than the behaviour changing unnoticed.
        assertEquals(4f, HorizontalSolarOutput.stressCapacity(SolarFacing.EAST, 0, 1, false), 1e-6);
        assertEquals(HorizontalSolarOutput.MIN_CAPACITY,
                HorizontalSolarOutput.stressCapacity(SolarFacing.EAST, DayCycle.DAYLIGHT_END, 1, false), 1e-6);
    }
}
