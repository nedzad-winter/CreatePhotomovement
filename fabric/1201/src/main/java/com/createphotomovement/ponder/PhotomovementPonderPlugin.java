package com.createphotomovement.ponder;

import com.createphotomovement.AllBlocks;
import com.createphotomovement.CreatePhotomovement;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

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
        // Create a helper that works with Block by converting to ResourceLocation
        PonderSceneRegistrationHelper<Block> HELPER = helper.withKeyFunction(
                block -> BuiltInRegistries.BLOCK.getKey(block));

        // Register Solar Generator scenes with Create's Kinetic Sources tag
        HELPER.forComponents(AllBlocks.SOLAR_GENERATOR)
                .addStoryBoard("solar_generator/basics", SolarGeneratorScenes::basics,
                        AllCreatePonderTags.KINETIC_SOURCES)
                .addStoryBoard("solar_generator/weather", SolarGeneratorScenes::weather)
                .addStoryBoard("solar_generator/obstructions", SolarGeneratorScenes::obstructions)
                .addStoryBoard("solar_generator/dyeing", SolarGeneratorScenes::dyeing);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        // Use Create's existing tags
    }
}
