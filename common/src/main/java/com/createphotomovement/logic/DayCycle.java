package com.createphotomovement.logic;

/**
 * Where in the Minecraft day a given tick falls.
 *
 * <p>
 * All methods take the raw {@code level.getDayTime()} value and do the
 * wrapping themselves, so callers never have to remember the modulo.
 */
public final class DayCycle {

    /** Length of a full Minecraft day in ticks. */
    public static final long DAY_LENGTH = 24000L;

    /** First tick counted as night. */
    public static final long NIGHT_START = 13000L;

    /** First tick counted as day again. */
    public static final long NIGHT_END = 23000L;

    /** The daylight half of the cycle, used for the output curve. */
    public static final long DAYLIGHT_END = 12000L;

    private DayCycle() {
    }

    /** Wraps a raw day time into the range {@code [0, DAY_LENGTH)}. */
    public static long timeOfDay(long dayTime) {
        long wrapped = dayTime % DAY_LENGTH;
        return wrapped < 0 ? wrapped + DAY_LENGTH : wrapped;
    }

    /** Whether the sun is down. Matches {@code dayTime >= 13000 && dayTime < 23000}. */
    public static boolean isNight(long dayTime) {
        long time = timeOfDay(dayTime);
        return time >= NIGHT_START && time < NIGHT_END;
    }

    /**
     * How far through the daylight half of the day we are, from 0.0 at dawn to
     * 1.0 at dusk.
     *
     * <p>
     * Everything past {@link #DAYLIGHT_END} clamps to 1.0, so the whole night
     * reads as "dusk". The output curve only uses this while the generator has
     * sky access anyway.
     */
    public static float daylightRatio(long dayTime) {
        long time = Math.min(timeOfDay(dayTime), DAYLIGHT_END);
        return clamp((float) time / (float) DAYLIGHT_END, 0f, 1f);
    }

    /** {@code Mth.clamp} without the Minecraft dependency. */
    public static float clamp(float value, float min, float max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    /** {@code Mth.clamp} without the Minecraft dependency. */
    public static int clamp(int value, int min, int max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
}
