package com.createphotomovement;

import com.createphotomovement.content.kinetics.solarwindmill.SolarBearingContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class AllContraptionTypes {

    public static final ContraptionType SOLAR_BEARING = new ContraptionType(SolarBearingContraption::new);

    public static void register() {
        Registry.register(CreateBuiltInRegistries.CONTRAPTION_TYPE,
                new ResourceLocation(CreatePhotomovement.MOD_ID, "solar_bearing"),
                SOLAR_BEARING);
    }
}
