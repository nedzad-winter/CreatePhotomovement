package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AllBlockEntityTypes {
        public static BlockEntityType<SolarGeneratorBlockEntity> SOLAR_GENERATOR;
        public static BlockEntityType<HorizontalSolarGeneratorBlockEntity> HORIZONTAL_SOLAR_GENERATOR;

        public static void register() {
                SOLAR_GENERATOR = Registry.register(
                                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                                new ResourceLocation(CreatePhotomovement.MOD_ID, "solar_generator"),
                                FabricBlockEntityTypeBuilder.create(
                                                (pos, state) -> new SolarGeneratorBlockEntity(SOLAR_GENERATOR, pos,
                                                                state),
                                                AllBlocks.SOLAR_GENERATOR,
                                                AllBlocks.WHITE_SOLAR_GENERATOR,
                                                AllBlocks.ORANGE_SOLAR_GENERATOR,
                                                AllBlocks.MAGENTA_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR,
                                                AllBlocks.YELLOW_SOLAR_GENERATOR,
                                                AllBlocks.LIME_SOLAR_GENERATOR,
                                                AllBlocks.PINK_SOLAR_GENERATOR,
                                                AllBlocks.GRAY_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR,
                                                AllBlocks.CYAN_SOLAR_GENERATOR,
                                                AllBlocks.PURPLE_SOLAR_GENERATOR,
                                                AllBlocks.BLUE_SOLAR_GENERATOR,
                                                AllBlocks.BROWN_SOLAR_GENERATOR,
                                                AllBlocks.GREEN_SOLAR_GENERATOR,
                                                AllBlocks.RED_SOLAR_GENERATOR,
                                                AllBlocks.BLACK_SOLAR_GENERATOR).build());

                HORIZONTAL_SOLAR_GENERATOR = Registry.register(
                                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                                new ResourceLocation(CreatePhotomovement.MOD_ID, "horizontal_solar_generator"),
                                FabricBlockEntityTypeBuilder.create(
                                                (pos, state) -> new HorizontalSolarGeneratorBlockEntity(
                                                                HORIZONTAL_SOLAR_GENERATOR, pos, state),
                                                AllBlocks.HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.WHITE_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.ORANGE_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.MAGENTA_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.PINK_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.GRAY_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.CYAN_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.PURPLE_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.BROWN_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.GREEN_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR,
                                                AllBlocks.BLACK_HORIZONTAL_SOLAR_GENERATOR).build());
        }
}
