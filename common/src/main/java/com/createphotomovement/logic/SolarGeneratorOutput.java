package com.createphotomovement.logic;

/**
 * Rotation speed of the vertical solar generators.
 *
 * <p>
 * Whether the generator can see the sky at all is a world question and stays in
 * the block entity; this class only answers "given that it can generate, how
 * fast".
 */
public final class SolarGeneratorOutput {

    /** The basic generator runs at the configured speed. */
    public static final int BASIC_MULTIPLIER = 1;

    /** The brass-cased advanced generator runs at double. */
    public static final int ADVANCED_MULTIPLIER = 2;

    /**
     * Effective sky light a panel needs before it generates anything.
     *
     * <p>
     * Compared against {@code getBrightness(SKY, pos) - getSkyDarken()}, which
     * is what makes the generator stop at dusk rather than at midnight.
     */
    public static final int MIN_SKY_LIGHT = 12;

    private SolarGeneratorOutput() {
    }

    /**
     * @param configuredSpeed {@code PMConfigs.server().generationSpeed}, in RPM
     * @param multiplier      {@link #BASIC_MULTIPLIER} or
     *                        {@link #ADVANCED_MULTIPLIER}
     * @param rainingOnPanel  whether it is raining on the block the panel faces
     * @return generated speed in RPM
     */
    public static float generatedSpeed(int configuredSpeed, int multiplier, boolean rainingOnPanel) {
        float speed = (float) configuredSpeed * multiplier;
        if (rainingOnPanel)
            speed = speed / 2;
        return speed;
    }
}
