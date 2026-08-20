package com.createphotomovement.logic;

/**
 * The horizontal directions a solar generator can track the sun in.
 *
 * <p>
 * Only east and west follow the sun; north and south collapse into
 * {@link #OTHER} because they behave identically (minimum output all day).
 * Keeping this separate from Minecraft's {@code Direction} is what allows the
 * output curve to be unit-tested without starting the game.
 */
public enum SolarFacing {

    /** Peaks at dawn, falls off towards dusk. */
    EAST,

    /** Rises towards dusk, minimal at dawn. */
    WEST,

    /** North and south: no sun tracking, minimum output. */
    OTHER
}
