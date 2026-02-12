package com.createphotomovement;

import com.createphotomovement.content.kinetics.solarwindmill.SolarBearingContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AllContraptionTypes {

    public static final DeferredRegister<ContraptionType> CONTRAPTION_TYPES = DeferredRegister
            .create(CreateRegistries.CONTRAPTION_TYPE, CreatePhotomovement.MOD_ID);

    public static final RegistryObject<ContraptionType> SOLAR_BEARING = CONTRAPTION_TYPES
            .register("solar_bearing", () -> new ContraptionType(SolarBearingContraption::new));

    public static void register(IEventBus modEventBus) {
        CONTRAPTION_TYPES.register(modEventBus);
    }
}
