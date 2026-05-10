package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.AdvSolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.HorzAdvSolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlock;
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
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> HORIZONTAL_SOLAR_GENERATOR = BLOCKS.register(
                        "horizontal_solar_generator",
                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        // Horizontal Solar Generator Color Variants
        public static final DeferredBlock<HorizontalSolarGeneratorBlock> WHITE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "white_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.SNOW)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> ORANGE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "orange_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_ORANGE)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> MAGENTA_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "magenta_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_MAGENTA)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "light_blue_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> YELLOW_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "yellow_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_YELLOW)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> LIME_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "lime_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> PINK_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "pink_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_PINK)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> GRAY_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "gray_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_GRAY)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "light_gray_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> CYAN_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "cyan_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_CYAN)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> PURPLE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "purple_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_PURPLE)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> BLUE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "blue_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_BLUE)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> BROWN_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "brown_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_BROWN)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> GREEN_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "green_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_GREEN)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> RED_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "red_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_RED)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorizontalSolarGeneratorBlock> BLACK_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "black_horizontal_solar_generator",
                                        () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_BLACK)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        // Stained Glass Variants
        public static final DeferredBlock<SolarGeneratorBlock> WHITE_SOLAR_GENERATOR = BLOCKS.register(
                        "white_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SNOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> ORANGE_SOLAR_GENERATOR = BLOCKS.register(
                        "orange_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> MAGENTA_SOLAR_GENERATOR = BLOCKS.register(
                        "magenta_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_MAGENTA)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> LIGHT_BLUE_SOLAR_GENERATOR = BLOCKS.register(
                        "light_blue_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> YELLOW_SOLAR_GENERATOR = BLOCKS.register(
                        "yellow_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_YELLOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> LIME_SOLAR_GENERATOR = BLOCKS.register(
                        "lime_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> PINK_SOLAR_GENERATOR = BLOCKS.register(
                        "pink_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PINK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> GRAY_SOLAR_GENERATOR = BLOCKS.register(
                        "gray_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> LIGHT_GRAY_SOLAR_GENERATOR = BLOCKS.register(
                        "light_gray_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> CYAN_SOLAR_GENERATOR = BLOCKS.register(
                        "cyan_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_CYAN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> PURPLE_SOLAR_GENERATOR = BLOCKS.register(
                        "purple_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> BLUE_SOLAR_GENERATOR = BLOCKS.register(
                        "blue_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> BROWN_SOLAR_GENERATOR = BLOCKS.register(
                        "brown_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BROWN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> GREEN_SOLAR_GENERATOR = BLOCKS.register(
                        "green_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> RED_SOLAR_GENERATOR = BLOCKS.register(
                        "red_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<SolarGeneratorBlock> BLACK_SOLAR_GENERATOR = BLOCKS.register(
                        "black_solar_generator",
                        () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        // Advanced Solar Generator Color Variants
        public static final DeferredBlock<AdvSolarGeneratorBlock> WHITE_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "white_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SNOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> ORANGE_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "orange_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> MAGENTA_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "magenta_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_MAGENTA)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> LIGHT_BLUE_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "light_blue_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> YELLOW_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "yellow_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_YELLOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> LIME_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "lime_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> PINK_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "pink_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PINK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> GRAY_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "gray_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> LIGHT_GRAY_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "light_gray_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> CYAN_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "cyan_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_CYAN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> PURPLE_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "purple_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> BLUE_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "blue_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> BROWN_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "brown_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BROWN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> GREEN_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "green_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> RED_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "red_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<AdvSolarGeneratorBlock> BLACK_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "black_adv_solar_generator",
                        () -> new AdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        // Horizontal Advanced Solar Generator Color Variants
        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> WHITE_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "white_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SNOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> ORANGE_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "orange_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_ORANGE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> MAGENTA_HORZ_ADV_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "magenta_horz_adv_solar_generator",
                                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_MAGENTA)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "light_blue_horz_adv_solar_generator",
                                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> YELLOW_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "yellow_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_YELLOW)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> LIME_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "lime_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> PINK_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "pink_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PINK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> GRAY_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "gray_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GRAY)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "light_gray_horz_adv_solar_generator",
                                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> CYAN_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "cyan_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_CYAN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> PURPLE_HORZ_ADV_SOLAR_GENERATOR = BLOCKS
                        .register(
                                        "purple_horz_adv_solar_generator",
                                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.COLOR_PURPLE)
                                                        .strength(1.5F)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> BLUE_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "blue_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> BROWN_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "brown_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BROWN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> GREEN_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "green_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> RED_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "red_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<HorzAdvSolarGeneratorBlock> BLACK_HORZ_ADV_SOLAR_GENERATOR = BLOCKS.register(
                        "black_horz_adv_solar_generator",
                        () -> new HorzAdvSolarGeneratorBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .strength(1.5F)
                                        .sound(SoundType.WOOD)
                                        .noOcclusion()));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> SOLAR_SAIL = BLOCKS
                        .register("solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(net.minecraft.world.level.material.MapColor.WOOL)
                                                                        .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                                                        .strength(0.8F),
                                                                        net.minecraft.world.item.DyeColor.WHITE));
        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> ORANGE_SOLAR_SAIL = BLOCKS
                        .register("orange_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_ORANGE)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.ORANGE));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> MAGENTA_SOLAR_SAIL = BLOCKS
                        .register("magenta_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_MAGENTA)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.MAGENTA));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> LIGHT_BLUE_SOLAR_SAIL = BLOCKS
                        .register("light_blue_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.LIGHT_BLUE));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> YELLOW_SOLAR_SAIL = BLOCKS
                        .register("yellow_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_YELLOW)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.YELLOW));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> LIME_SOLAR_SAIL = BLOCKS
                        .register("lime_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.LIME));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> PINK_SOLAR_SAIL = BLOCKS
                        .register("pink_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_PINK)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.PINK));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> GRAY_SOLAR_SAIL = BLOCKS
                        .register("gray_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_GRAY)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.GRAY));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> LIGHT_GRAY_SOLAR_SAIL = BLOCKS
                        .register("light_gray_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.LIGHT_GRAY));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> CYAN_SOLAR_SAIL = BLOCKS
                        .register("cyan_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_CYAN)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.CYAN));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> PURPLE_SOLAR_SAIL = BLOCKS
                        .register("purple_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_PURPLE)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.PURPLE));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> BLUE_SOLAR_SAIL = BLOCKS
                        .register("blue_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_BLUE)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.BLUE));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> BROWN_SOLAR_SAIL = BLOCKS
                        .register("brown_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_BROWN)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.BROWN));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> GREEN_SOLAR_SAIL = BLOCKS
                        .register("green_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_GREEN)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.GREEN));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> RED_SOLAR_SAIL = BLOCKS
                        .register("red_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_RED)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.RED));

        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock> BLACK_SOLAR_SAIL = BLOCKS
                        .register("black_solar_sail",
                                        () -> com.createphotomovement.content.kinetics.solarwindmill.SolarSailBlock
                                                        .withCanvas(BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.COLOR_BLACK)
                                                                        .strength(0.1F)
                                                                        .sound(SoundType.SCAFFOLDING)
                                                                        .noOcclusion(),
                                                                        net.minecraft.world.item.DyeColor.BLACK));

        // Solar Windmill Bearing
        public static final DeferredBlock<com.createphotomovement.content.kinetics.solarwindmill.SolarWindmillBearingBlock> SOLAR_WINDMILL_BEARING = BLOCKS
                        .register("solar_windmill_bearing",
                                        () -> new com.createphotomovement.content.kinetics.solarwindmill.SolarWindmillBearingBlock(
                                                        BlockBehaviour.Properties.of()
                                                                        .mapColor(MapColor.PODZOL)
                                                                        .strength(3.0F, 6.0F)
                                                                        .sound(SoundType.WOOD)
                                                                        .noOcclusion()));
}
