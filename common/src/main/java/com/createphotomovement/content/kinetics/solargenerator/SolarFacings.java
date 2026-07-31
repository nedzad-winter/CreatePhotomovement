package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.logic.SolarFacing;

import net.minecraft.core.Direction;

/**
 * Bridges Minecraft's {@link Direction} to the loader-independent
 * {@link SolarFacing}.
 *
 * <p>
 * This lives apart from the {@code logic} package on purpose: everything in
 * {@code logic} stays free of Minecraft imports so it can be unit-tested
 * without starting the game, and this one mapping is the seam.
 */
public final class SolarFacings {

    private SolarFacings() {
    }

    public static SolarFacing of(Direction direction) {
        if (direction == Direction.EAST)
            return SolarFacing.EAST;
        if (direction == Direction.WEST)
            return SolarFacing.WEST;
        return SolarFacing.OTHER;
    }
}
