package com.createphotomovement.logic;

/**
 * Speed and stress capacity of the solar windmill bearing.
 *
 * <p>
 * Sails are counted in brackets: every full group of {@code sailsPerBracket}
 * sails adds one RPM and {@link #SU_PER_BRACKET} stress units. Solar sails earn
 * the same brackets but their contribution is scaled by
 * {@link #solarMultiplier}.
 *
 * <p>
 * <strong>The bearing does not stop at night.</strong> Unlike the solar
 * generators, which produce nothing without sunlight, a solar windmill bearing
 * at night is simply an ordinary windmill bearing: same speed, and solar sails
 * worth exactly as much as regular ones. That falls out of the split below --
 * {@link #generatedSpeed} takes neither a day time nor a weather, so rotation
 * cannot depend on them, and only {@link #solarMultiplier} knows the time.
 * Keep it that way.
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
     * For regular sails this works out to a flat {@link #SU_PER_BRACKET} per RPM
     * at every sail count, because the bracket count appears in both the
     * numerator and the divisor. The total the player sees grows only because the
     * <em>speed</em> grows with the sail count.
     *
     * <p>
     * That has a consequence worth knowing: {@link #generatedSpeed} caps at
     * {@link #MAX_RPM}, so the total stress capacity saturates once the sails fill
     * 16 brackets (128 sails at Create's default). Sails beyond that add nothing
     * at all. Whether that cap is intended is an open question -- see
     * {@code docs/common-code-analysis.md}.
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
