package com.createphotomovement.logic;

/**
 * Stress capacity of the horizontal solar generators over the course of a day.
 *
 * <p>
 * East-facing panels peak at dawn and fall off quadratically; west-facing ones
 * do the reverse. North and south never track the sun and sit at the minimum.
 * An obstruction in front of the panel pins the output to the minimum
 * regardless of time of day.
 */
public final class HorizontalSolarOutput {

    /** Floor output -- an unobstructed panel never drops below this. */
    public static final float MIN_CAPACITY = 8f;

    /** Peak is this multiple of the configured base capacity. */
    public static final int PEAK_MULTIPLIER = 4;

    /**
     * First block in front of the panel scanned for a distant obstruction. The
     * block directly in front (distance 1) stops generation outright and is
     * checked separately.
     */
    public static final int OBSTRUCTION_SCAN_FROM = 2;

    /** Last block in front of the panel scanned for a distant obstruction. */
    public static final int OBSTRUCTION_SCAN_TO = 10;

    private HorizontalSolarOutput() {
    }

    /**
     * @param facing        which way the panel points
     * @param dayTime       raw {@code level.getDayTime()}
     * @param baseCapacity  {@code PMConfigs.server().stressCapacity}, in SU per RPM
     * @param obstructed    whether a solid block sits 2..10 blocks in front
     * @return stress capacity in SU per RPM, rounded to a whole number
     */
    public static float stressCapacity(SolarFacing facing, long dayTime, int baseCapacity, boolean obstructed) {
        if (obstructed)
            return MIN_CAPACITY;

        float peak = (float) PEAK_MULTIPLIER * baseCapacity;
        float ratio = DayCycle.daylightRatio(dayTime);

        float factor;
        switch (facing) {
            case EAST:
                // Starts high, goes low.
                factor = (1 - ratio) * (1 - ratio);
                break;
            case WEST:
                // Starts low, goes high.
                factor = ratio * ratio;
                break;
            default:
                return MIN_CAPACITY;
        }

        return Math.round(MIN_CAPACITY + (peak - MIN_CAPACITY) * factor);
    }
}
