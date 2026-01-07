package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public class AllBlocks {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreatePhotomovement.MOD_ID);

        public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR = BLOCKS.register("solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        // Stained Glass Variants
        public static final DeferredBlock<SolarGeneratorBlock> WHITE_SOLAR_GENERATOR = BLOCKS.register(
                        "white_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SNOW)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> ORANGE_SOLAR_GENERATOR = BLOCKS.register(
                        "orange_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> MAGENTA_SOLAR_GENERATOR = BLOCKS.register(
                        "magenta_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_MAGENTA)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> LIGHT_BLUE_SOLAR_GENERATOR = BLOCKS.register(
                        "light_blue_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> YELLOW_SOLAR_GENERATOR = BLOCKS.register(
                        "yellow_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_YELLOW)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> LIME_SOLAR_GENERATOR = BLOCKS.register(
                        "lime_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> PINK_SOLAR_GENERATOR = BLOCKS.register(
                        "pink_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PINK)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> GRAY_SOLAR_GENERATOR = BLOCKS.register(
                        "gray_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GRAY)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> LIGHT_GRAY_SOLAR_GENERATOR = BLOCKS.register(
                        "light_gray_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> CYAN_SOLAR_GENERATOR = BLOCKS.register(
                        "cyan_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_CYAN)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> PURPLE_SOLAR_GENERATOR = BLOCKS.register(
                        "purple_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> BLUE_SOLAR_GENERATOR = BLOCKS.register(
                        "blue_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> BROWN_SOLAR_GENERATOR = BLOCKS.register(
                        "brown_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BROWN)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> GREEN_SOLAR_GENERATOR = BLOCKS.register(
                        "green_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> RED_SOLAR_GENERATOR = BLOCKS.register(
                        "red_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> BLACK_SOLAR_GENERATOR = BLOCKS.register(
                        "black_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .strength(0.2F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));
}
