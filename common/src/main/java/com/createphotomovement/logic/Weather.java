package com.createphotomovement.logic;

/**
 * Weather as far as power generation is concerned.
 *
 * <p>
 * Minecraft reports rain and thunder as two independent booleans, and a
 * thunderstorm has both set. Collapsing them into one value here means callers
 * cannot accidentally treat a thunderstorm as ordinary rain.
 */
public enum Weather {

    CLEAR,
    RAIN,
    THUNDER;

    /**
     * Maps Minecraft's two booleans onto a single value. Thunder wins over rain,
     * matching {@code level.isRaining() && !level.isThundering()}.
     */
    public static Weather of(boolean raining, boolean thundering) {
        if (thundering)
            return THUNDER;
        if (raining)
            return RAIN;
        return CLEAR;
    }
}
