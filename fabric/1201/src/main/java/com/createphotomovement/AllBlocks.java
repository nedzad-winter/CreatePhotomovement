package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AllBlocks {
        // Main blocks
        public static final SolarGeneratorBlock SOLAR_GENERATOR = new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(1.5F)
                        .sound(SoundType.WOOD)
                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        // Horizontal Solar Generator Color Variants
        public static final HorizontalSolarGeneratorBlock WHITE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SNOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock ORANGE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock MAGENTA_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_MAGENTA)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock YELLOW_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_YELLOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock LIME_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock PINK_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PINK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock GRAY_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock CYAN_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_CYAN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock PURPLE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock BLUE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock BROWN_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BROWN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock GREEN_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock RED_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final HorizontalSolarGeneratorBlock BLACK_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        // Solar Generator Color Variants
        public static final SolarGeneratorBlock WHITE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SNOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock ORANGE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock MAGENTA_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_MAGENTA)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock LIGHT_BLUE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock YELLOW_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_YELLOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock LIME_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock PINK_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PINK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock GRAY_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock LIGHT_GRAY_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock CYAN_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_CYAN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock PURPLE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock BLUE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock BROWN_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BROWN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock GREEN_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock RED_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static final SolarGeneratorBlock BLACK_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion());

        public static void register() {
                registerBlock("solar_generator", SOLAR_GENERATOR);
                registerBlock("horizontal_solar_generator", HORIZONTAL_SOLAR_GENERATOR);

                // Horizontal Solar Generator Color Variants
                registerBlock("white_horizontal_solar_generator", WHITE_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("orange_horizontal_solar_generator", ORANGE_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("magenta_horizontal_solar_generator", MAGENTA_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("light_blue_horizontal_solar_generator", LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("yellow_horizontal_solar_generator", YELLOW_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("lime_horizontal_solar_generator", LIME_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("pink_horizontal_solar_generator", PINK_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("gray_horizontal_solar_generator", GRAY_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("light_gray_horizontal_solar_generator", LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("cyan_horizontal_solar_generator", CYAN_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("purple_horizontal_solar_generator", PURPLE_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("blue_horizontal_solar_generator", BLUE_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("brown_horizontal_solar_generator", BROWN_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("green_horizontal_solar_generator", GREEN_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("red_horizontal_solar_generator", RED_HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("black_horizontal_solar_generator", BLACK_HORIZONTAL_SOLAR_GENERATOR);

                // Solar Generator Color Variants
                registerBlock("white_solar_generator", WHITE_SOLAR_GENERATOR);
                registerBlock("orange_solar_generator", ORANGE_SOLAR_GENERATOR);
                registerBlock("magenta_solar_generator", MAGENTA_SOLAR_GENERATOR);
                registerBlock("light_blue_solar_generator", LIGHT_BLUE_SOLAR_GENERATOR);
                registerBlock("yellow_solar_generator", YELLOW_SOLAR_GENERATOR);
                registerBlock("lime_solar_generator", LIME_SOLAR_GENERATOR);
                registerBlock("pink_solar_generator", PINK_SOLAR_GENERATOR);
                registerBlock("gray_solar_generator", GRAY_SOLAR_GENERATOR);
                registerBlock("light_gray_solar_generator", LIGHT_GRAY_SOLAR_GENERATOR);
                registerBlock("cyan_solar_generator", CYAN_SOLAR_GENERATOR);
                registerBlock("purple_solar_generator", PURPLE_SOLAR_GENERATOR);
                registerBlock("blue_solar_generator", BLUE_SOLAR_GENERATOR);
                registerBlock("brown_solar_generator", BROWN_SOLAR_GENERATOR);
                registerBlock("green_solar_generator", GREEN_SOLAR_GENERATOR);
                registerBlock("red_solar_generator", RED_SOLAR_GENERATOR);
                registerBlock("black_solar_generator", BLACK_SOLAR_GENERATOR);
        }

        private static void registerBlock(String name, Block block) {
                Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(CreatePhotomovement.MOD_ID, name),
                                block);
        }
}
