package com.createphotomovement.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WeatherTest {

    @Test
    void clearWhenNeitherFlagIsSet() {
        assertEquals(Weather.CLEAR, Weather.of(false, false));
    }

    @Test
    void rainWhenOnlyRaining() {
        assertEquals(Weather.RAIN, Weather.of(true, false));
    }

    @Test
    @DisplayName("a thunderstorm is thunder, not rain")
    void thunderWinsOverRain() {
        // Minecraft sets both flags during a thunderstorm. Reading isRaining() alone
        // would hand a thunderstorm the reduced rain bonus instead of no bonus.
        assertEquals(Weather.THUNDER, Weather.of(true, true));
    }

    @Test
    @DisplayName("thundering without raining is still thunder")
    void thunderWithoutRain() {
        assertEquals(Weather.THUNDER, Weather.of(false, true));
    }
}
