package com.createphotomovement.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class PMServer extends ConfigBase {

    public final ConfigInt generationSpeed = i(16, 1, "generationSpeed", "Speed of the Solar Generator in RPM");
    public final ConfigInt stressCapacity = i(16, 1, "stressCapacity",
            "Stress Capacity of the Solar Generator in SU per RPM");

    @Override
    public String getName() {
        return "server";
    }
}
