package com.createphotomovement.logic;

/**
 * Speed and stress capacity of the solar windmill bearing.
 *
 * <p>
 * Sails are counted in brackets: every full group of {@code sailsPerBracket}
 * sails adds one RPM and {@link #SU_PER_BRACKET} stress units. Solar sails earn
 * the same brackets but their contribution is scaled by
 * {@link #solarMultiplier}.
 */
public final class WindmillOutput {

    /** Stress units contributed by one full bracket of sails. */
    public static final float SU_PER_BRACKET = 512f;

    /** A bearing always turns at least this fast once running. */
    public static final int MIN_RPM = 1;

    /** Speed caps here no matter how many sails are attached. */
    public static final int MAX_RPM = 16;

    /** No bonus: night, thunder, or no sky access. */
    public static final float MULTIPLIER_NONE = 1.0f;

    /** Reduced bonus while it rains. */
    public static final float MULTIPLIER_RAIN = 1.5f;

    /** Full bonus on a clear day with sky access. */
    public static final float MULTIPLIER_CLEAR = 2.0f;

    private WindmillOutput() {
    }

    /**
     * Rotation speed in RPM, signed by the direction of travel.
     *
     * <p>
     * Note this clamps to {@link #MAX_RPM}, while
     * {@link #stressCapacityPerRpm} deliberately does not -- see the note there.
     *
     * @param direction Create's angle speed direction, {@code +1} or {@code -1}
     */
    public static float generatedSpeed(int regularSails, int solarSails, int sailsPerBracket, float direction) {
        requireValidBracket(sailsPerBracket);
        int rpm = (regularSails + solarSails) / sailsPerBracket;
        return DayCycle.clamp(rpm, MIN_RPM, MAX_RPM) * direction;
    }

    /**
     * The bonus applied to stress units from solar sails.
     *
     * <p>
     * Sky access is checked first: a covered array gets no bonus regardless of
     * the weather above it.
     */
    public static float solarMultiplier(boolean hasSkyAccess, long dayTime, Weather weather) {
        if (!hasSkyAccess)
            return MULTIPLIER_NONE;
        if (DayCycle.isNight(dayTime))
            return MULTIPLIER_NONE;
        if (weather == Weather.THUNDER)
            return MULTIPLIER_NONE;
        if (weather == Weather.RAIN)
            return MULTIPLIER_RAIN;
        return MULTIPLIER_CLEAR;
    }

    /**
     * Stress capacity in SU per RPM.
     *
     * <p>
     * Create multiplies the reported capacity by the current speed, so the total
     * SU is divided by the RPM here to make the displayed figure match the table
     * in the README.
     *
     * <p>
     * The RPM used for that division is floored at {@link #MIN_RPM} but is
     * <em>not</em> capped at {@link #MAX_RPM}, unlike
     * {@link #generatedSpeed}. Above the cap the division therefore keeps
     * shrinking the per-RPM figure while the actual speed stays at 16, which is
     * what keeps the total SU growing linearly with the sail count.
     */
    public static float stressCapacityPerRpm(int regularSails, int solarSails, int sailsPerBracket,
            float solarMultiplier) {
        requireValidBracket(sailsPerBracket);

        float normalSU = (regularSails / sailsPerBracket) * SU_PER_BRACKET;
        float solarSU = (solarSails / sailsPerBracket) * SU_PER_BRACKET * solarMultiplier;

        int rpm = Math.max(MIN_RPM, (regularSails + solarSails) / sailsPerBracket);
        return (normalSU + solarSU) / rpm;
    }

    /**
     * Create's {@code windmillSailsPerRPM} config has a minimum of 1, so this can
     * only fire if a caller passes something it made up itself.
     */
    private static void requireValidBracket(int sailsPerBracket) {
        if (sailsPerBracket < 1)
            throw new IllegalArgumentException("sailsPerBracket must be at least 1, got " + sailsPerBracket);
    }
}
