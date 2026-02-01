package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.AdvSolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.HorzAdvSolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlockEntity;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AllBlockEntityTypes {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(Registries.BLOCK_ENTITY_TYPE, CreatePhotomovement.MOD_ID);

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HorizontalSolarGeneratorBlockEntity>> HORIZONTAL_SOLAR_GENERATOR = BLOCK_ENTITY_TYPES
                        .register("horizontal_solar_generator",
                                        () -> BlockEntityType.Builder.of(
                                                        (pos, state) -> new HorizontalSolarGeneratorBlockEntity(
                                                                        AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR
                                                                                        .get(),
                                                                        pos, state),
                                                        AllBlocks.HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.WHITE_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.ORANGE_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.MAGENTA_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PINK_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GRAY_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.CYAN_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PURPLE_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BROWN_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GREEN_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLACK_HORIZONTAL_SOLAR_GENERATOR.get())
                                                        .build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarGeneratorBlockEntity>> SOLAR_GENERATOR = BLOCK_ENTITY_TYPES
                        .register("solar_generator",
                                        () -> BlockEntityType.Builder.of(
                                                        (pos, state) -> new SolarGeneratorBlockEntity(
                                                                        AllBlockEntityTypes.SOLAR_GENERATOR.get(),
                                                                        pos, state),
                                                        AllBlocks.SOLAR_GENERATOR.get(),
                                                        AllBlocks.WHITE_SOLAR_GENERATOR.get(),
                                                        AllBlocks.ORANGE_SOLAR_GENERATOR.get(),
                                                        AllBlocks.MAGENTA_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR.get(),
                                                        AllBlocks.YELLOW_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIME_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PINK_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GRAY_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR.get(),
                                                        AllBlocks.CYAN_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PURPLE_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLUE_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BROWN_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GREEN_SOLAR_GENERATOR.get(),
                                                        AllBlocks.RED_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLACK_SOLAR_GENERATOR.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AdvSolarGeneratorBlockEntity>> ADV_SOLAR_GENERATOR = BLOCK_ENTITY_TYPES
                        .register("adv_solar_generator",
                                        () -> BlockEntityType.Builder.of(
                                                        (pos, state) -> new AdvSolarGeneratorBlockEntity(
                                                                        AllBlockEntityTypes.ADV_SOLAR_GENERATOR.get(),
                                                                        pos, state),
                                                        AllBlocks.ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.WHITE_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.ORANGE_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.MAGENTA_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_BLUE_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.YELLOW_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIME_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PINK_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GRAY_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_GRAY_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.CYAN_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PURPLE_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLUE_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BROWN_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GREEN_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.RED_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLACK_ADV_SOLAR_GENERATOR.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HorzAdvSolarGeneratorBlockEntity>> HORZ_ADV_SOLAR_GENERATOR = BLOCK_ENTITY_TYPES
                        .register("horz_adv_solar_generator",
                                        () -> BlockEntityType.Builder.of(
                                                        (pos, state) -> new HorzAdvSolarGeneratorBlockEntity(
                                                                        AllBlockEntityTypes.HORZ_ADV_SOLAR_GENERATOR
                                                                                        .get(),
                                                                        pos, state),
                                                        AllBlocks.HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.WHITE_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.ORANGE_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.MAGENTA_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.YELLOW_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIME_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PINK_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GRAY_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.CYAN_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.PURPLE_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLUE_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BROWN_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.GREEN_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.RED_HORZ_ADV_SOLAR_GENERATOR.get(),
                                                        AllBlocks.BLACK_HORZ_ADV_SOLAR_GENERATOR.get()).build(null));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.createphotomovement.content.kinetics.solarwindmill.SolarWindmillBearingBlockEntity>> SOLAR_WINDMILL_BEARING = BLOCK_ENTITY_TYPES
                        .register("solar_windmill_bearing",
                                        () -> BlockEntityType.Builder.of(
                                                        (pos, state) -> new com.createphotomovement.content.kinetics.solarwindmill.SolarWindmillBearingBlockEntity(
                                                                        AllBlockEntityTypes.SOLAR_WINDMILL_BEARING
                                                                                        .get(),
                                                                        pos, state),
                                                        AllBlocks.SOLAR_WINDMILL_BEARING.get()).build(null));
}
