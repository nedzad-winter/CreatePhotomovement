package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllBlocks {
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister
            .create(ForgeRegistries.BLOCKS, CreatePhotomovement.MOD_ID);

    public static final RegistryObject<SolarGeneratorBlock> SOLAR_GENERATOR = BLOCKS.register("solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> HORIZONTAL_SOLAR_GENERATOR = BLOCKS.register(
            "horizontal_solar_generator",
            () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    // Horizontal Solar Generator Color Variants
    public static final RegistryObject<HorizontalSolarGeneratorBlock> WHITE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("white_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SNOW)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> ORANGE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("orange_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> MAGENTA_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("magenta_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_MAGENTA)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("light_blue_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> YELLOW_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("yellow_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> LIME_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("lime_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GREEN)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> PINK_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("pink_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PINK)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> GRAY_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("gray_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GRAY)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("light_gray_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GRAY)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> CYAN_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("cyan_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_CYAN)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> PURPLE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("purple_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> BLUE_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("blue_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLUE)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> BROWN_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("brown_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> GREEN_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("green_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> RED_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("red_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_RED)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<HorizontalSolarGeneratorBlock> BLACK_HORIZONTAL_SOLAR_GENERATOR = BLOCKS
            .register("black_horizontal_solar_generator",
                    () -> new HorizontalSolarGeneratorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(1.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    // Stained Glass Variants
    public static final RegistryObject<SolarGeneratorBlock> WHITE_SOLAR_GENERATOR = BLOCKS.register(
            "white_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> ORANGE_SOLAR_GENERATOR = BLOCKS.register(
            "orange_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> MAGENTA_SOLAR_GENERATOR = BLOCKS.register(
            "magenta_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> LIGHT_BLUE_SOLAR_GENERATOR = BLOCKS.register(
            "light_blue_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> YELLOW_SOLAR_GENERATOR = BLOCKS.register(
            "yellow_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> LIME_SOLAR_GENERATOR = BLOCKS.register(
            "lime_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> PINK_SOLAR_GENERATOR = BLOCKS.register(
            "pink_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> GRAY_SOLAR_GENERATOR = BLOCKS.register(
            "gray_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> LIGHT_GRAY_SOLAR_GENERATOR = BLOCKS.register(
            "light_gray_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> CYAN_SOLAR_GENERATOR = BLOCKS.register(
            "cyan_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> PURPLE_SOLAR_GENERATOR = BLOCKS.register(
            "purple_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> BLUE_SOLAR_GENERATOR = BLOCKS.register(
            "blue_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> BROWN_SOLAR_GENERATOR = BLOCKS.register(
            "brown_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> GREEN_SOLAR_GENERATOR = BLOCKS.register(
            "green_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> RED_SOLAR_GENERATOR = BLOCKS.register(
            "red_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<SolarGeneratorBlock> BLACK_SOLAR_GENERATOR = BLOCKS.register(
            "black_solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));
}
