package com.createphotomovement.ponder;

import com.createphotomovement.AllBlocks;
import com.createphotomovement.CreatePhotomovement;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * Ponder plugin for Create Photomovement mod.
 * Registers Ponder scenes for the Solar Generator block.
 */
public class PhotomovementPonderPlugin implements PonderPlugin {

        @Override
        public String getModId() {
                return CreatePhotomovement.MOD_ID;
        }

        @Override
        public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
                // Create a helper that works with RegistryObject by converting to
                // ResourceLocation
                PonderSceneRegistrationHelper<RegistryObject<?>> HELPER = helper.withKeyFunction(ro -> ro.getId());

                // Register Solar Generator scenes with Create's Kinetic Sources tag
                HELPER.forComponents(AllBlocks.SOLAR_GENERATOR, AllBlocks.ADV_SOLAR_GENERATOR)
                                .addStoryBoard("solar_generator/solargenerator", SolarGeneratorScenes::basics,
                                                AllCreatePonderTags.KINETIC_SOURCES)
                                .addStoryBoard("solar_generator/weather", SolarGeneratorScenes::weather)
                                .addStoryBoard("solar_generator/obstructions", SolarGeneratorScenes::obstructions)
                                .addStoryBoard("solar_generator/dyeing", SolarGeneratorScenes::dyeing);

                // Register Horizontal Solar Generator scenes
                HELPER.forComponents(AllBlocks.HORIZONTAL_SOLAR_GENERATOR, AllBlocks.HORZ_ADV_SOLAR_GENERATOR)
                                .addStoryBoard("horizontal_solar_generator/basics",
                                                HorizontalSolarGeneratorScenes::basics,
                                                AllCreatePonderTags.KINETIC_SOURCES)
                                .addStoryBoard("horizontal_solar_generator/directions",
                                                HorizontalSolarGeneratorScenes::directions)
                                .addStoryBoard("horizontal_solar_generator/obstruction",
                                                HorizontalSolarGeneratorScenes::obstructions)
                                .addStoryBoard("horizontal_solar_generator/basics",
                                                HorizontalSolarGeneratorScenes::dyeing);

                HELPER.forComponents(AllBlocks.SOLAR_SAIL, AllBlocks.SOLAR_WINDMILL_BEARING)
                                .addStoryBoard("solarsail/solarsail", SolarSailScenes::basics)
                                .addStoryBoard("solarsail/solarsail_adv", SolarSailScenes::advanced);
        }

        @Override
        public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
                // Use Create's existing tags
        }
}
