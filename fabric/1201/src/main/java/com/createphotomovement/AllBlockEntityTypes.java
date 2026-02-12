package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.AdvSolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.HorzAdvSolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solarwindmill.SolarWindmillBearingBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AllBlockEntityTypes {
        public static BlockEntityType<SolarGeneratorBlockEntity> SOLAR_GENERATOR;
        public static BlockEntityType<HorizontalSolarGeneratorBlockEntity> HORIZONTAL_SOLAR_GENERATOR;
        public static BlockEntityType<AdvSolarGeneratorBlockEntity> ADV_SOLAR_GENERATOR;
        public static BlockEntityType<HorzAdvSolarGeneratorBlockEntity> HORZ_ADV_SOLAR_GENERATOR;
        public static BlockEntityType<SolarWindmillBearingBlockEntity> SOLAR_WINDMILL_BEARING;

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

                ADV_SOLAR_GENERATOR = Registry.register(
                                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                                new ResourceLocation(CreatePhotomovement.MOD_ID, "adv_solar_generator"),
                                FabricBlockEntityTypeBuilder.create(
                                                (pos, state) -> new AdvSolarGeneratorBlockEntity(ADV_SOLAR_GENERATOR,
                                                                pos, state),
                                                AllBlocks.ADV_SOLAR_GENERATOR,
                                                AllBlocks.WHITE_ADV_SOLAR_GENERATOR,
                                                AllBlocks.ORANGE_ADV_SOLAR_GENERATOR,
                                                AllBlocks.MAGENTA_ADV_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_BLUE_ADV_SOLAR_GENERATOR,
                                                AllBlocks.YELLOW_ADV_SOLAR_GENERATOR,
                                                AllBlocks.LIME_ADV_SOLAR_GENERATOR,
                                                AllBlocks.PINK_ADV_SOLAR_GENERATOR,
                                                AllBlocks.GRAY_ADV_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_GRAY_ADV_SOLAR_GENERATOR,
                                                AllBlocks.CYAN_ADV_SOLAR_GENERATOR,
                                                AllBlocks.PURPLE_ADV_SOLAR_GENERATOR,
                                                AllBlocks.BLUE_ADV_SOLAR_GENERATOR,
                                                AllBlocks.BROWN_ADV_SOLAR_GENERATOR,
                                                AllBlocks.GREEN_ADV_SOLAR_GENERATOR,
                                                AllBlocks.RED_ADV_SOLAR_GENERATOR,
                                                AllBlocks.BLACK_ADV_SOLAR_GENERATOR).build());

                HORZ_ADV_SOLAR_GENERATOR = Registry.register(
                                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                                new ResourceLocation(CreatePhotomovement.MOD_ID, "horz_adv_solar_generator"),
                                FabricBlockEntityTypeBuilder.create(
                                                (pos, state) -> new HorzAdvSolarGeneratorBlockEntity(
                                                                HORZ_ADV_SOLAR_GENERATOR, pos, state),
                                                AllBlocks.HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.WHITE_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.ORANGE_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.MAGENTA_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.YELLOW_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.LIME_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.PINK_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.GRAY_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.CYAN_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.PURPLE_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.BLUE_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.BROWN_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.GREEN_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.RED_HORZ_ADV_SOLAR_GENERATOR,
                                                AllBlocks.BLACK_HORZ_ADV_SOLAR_GENERATOR).build());

                SOLAR_WINDMILL_BEARING = Registry.register(
                                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                                new ResourceLocation(CreatePhotomovement.MOD_ID, "solar_windmill_bearing"),
                                FabricBlockEntityTypeBuilder.create(
                                                (pos, state) -> new SolarWindmillBearingBlockEntity(
                                                                SOLAR_WINDMILL_BEARING, pos, state),
                                                AllBlocks.SOLAR_WINDMILL_BEARING).build());
        }
}
