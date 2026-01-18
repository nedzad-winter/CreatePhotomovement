package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.AdvSolarGeneratorBlock;
import com.createphotomovement.content.kinetics.solargenerator.HorzAdvSolarGeneratorBlock;
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

        public static final AdvSolarGeneratorBlock ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.GOLD)
                                        .strength(2.0F)
                                        .sound(SoundType.METAL)
                                        .noOcclusion());

        public static final HorzAdvSolarGeneratorBlock HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.GOLD)
                                        .strength(2.0F)
                                        .sound(SoundType.METAL)
                                        .noOcclusion());

        // Horizontal Solar Generator Color Variants
        public static final HorizontalSolarGeneratorBlock WHITE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(1.5F).sound(SoundType.WOOD)
                                        .noOcclusion());
        public static final HorizontalSolarGeneratorBlock ORANGE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock MAGENTA_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock YELLOW_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock LIME_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock PINK_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock GRAY_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock CYAN_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock PURPLE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock BLUE_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock BROWN_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock GREEN_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final HorizontalSolarGeneratorBlock RED_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.5F).sound(SoundType.WOOD)
                                        .noOcclusion());
        public static final HorizontalSolarGeneratorBlock BLACK_HORIZONTAL_SOLAR_GENERATOR = new HorizontalSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());

        // Solar Generator Color Variants
        public static final SolarGeneratorBlock WHITE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(1.5F).sound(SoundType.WOOD)
                                        .noOcclusion());
        public static final SolarGeneratorBlock ORANGE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock MAGENTA_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock LIGHT_BLUE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock YELLOW_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock LIME_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock PINK_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock GRAY_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock LIGHT_GRAY_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock CYAN_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock PURPLE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock BLUE_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock BROWN_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock GREEN_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());
        public static final SolarGeneratorBlock RED_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.5F).sound(SoundType.WOOD)
                                        .noOcclusion());
        public static final SolarGeneratorBlock BLACK_SOLAR_GENERATOR = new SolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F)
                                        .sound(SoundType.WOOD).noOcclusion());

        // Advanced Solar Generator Color Variants
        public static final AdvSolarGeneratorBlock WHITE_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(2.0F).sound(SoundType.METAL)
                                        .noOcclusion());
        public static final AdvSolarGeneratorBlock ORANGE_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock MAGENTA_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock LIGHT_BLUE_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock YELLOW_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock LIME_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock PINK_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock GRAY_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock LIGHT_GRAY_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock CYAN_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock PURPLE_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock BLUE_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock BROWN_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock GREEN_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock RED_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final AdvSolarGeneratorBlock BLACK_ADV_SOLAR_GENERATOR = new AdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());

        // Horizontal Advanced Solar Generator Color Variants
        public static final HorzAdvSolarGeneratorBlock WHITE_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(2.0F).sound(SoundType.METAL)
                                        .noOcclusion());
        public static final HorzAdvSolarGeneratorBlock ORANGE_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock MAGENTA_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock YELLOW_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock LIME_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock PINK_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock GRAY_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock CYAN_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock PURPLE_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock BLUE_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock BROWN_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock GREEN_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock RED_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());
        public static final HorzAdvSolarGeneratorBlock BLACK_HORZ_ADV_SOLAR_GENERATOR = new HorzAdvSolarGeneratorBlock(
                        BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F)
                                        .sound(SoundType.METAL).noOcclusion());

        public static void register() {
                // Base blocks
                registerBlock("solar_generator", SOLAR_GENERATOR);
                registerBlock("horizontal_solar_generator", HORIZONTAL_SOLAR_GENERATOR);
                registerBlock("adv_solar_generator", ADV_SOLAR_GENERATOR);
                registerBlock("horz_adv_solar_generator", HORZ_ADV_SOLAR_GENERATOR);

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

                // Advanced Solar Generator Color Variants
                registerBlock("white_adv_solar_generator", WHITE_ADV_SOLAR_GENERATOR);
                registerBlock("orange_adv_solar_generator", ORANGE_ADV_SOLAR_GENERATOR);
                registerBlock("magenta_adv_solar_generator", MAGENTA_ADV_SOLAR_GENERATOR);
                registerBlock("light_blue_adv_solar_generator", LIGHT_BLUE_ADV_SOLAR_GENERATOR);
                registerBlock("yellow_adv_solar_generator", YELLOW_ADV_SOLAR_GENERATOR);
                registerBlock("lime_adv_solar_generator", LIME_ADV_SOLAR_GENERATOR);
                registerBlock("pink_adv_solar_generator", PINK_ADV_SOLAR_GENERATOR);
                registerBlock("gray_adv_solar_generator", GRAY_ADV_SOLAR_GENERATOR);
                registerBlock("light_gray_adv_solar_generator", LIGHT_GRAY_ADV_SOLAR_GENERATOR);
                registerBlock("cyan_adv_solar_generator", CYAN_ADV_SOLAR_GENERATOR);
                registerBlock("purple_adv_solar_generator", PURPLE_ADV_SOLAR_GENERATOR);
                registerBlock("blue_adv_solar_generator", BLUE_ADV_SOLAR_GENERATOR);
                registerBlock("brown_adv_solar_generator", BROWN_ADV_SOLAR_GENERATOR);
                registerBlock("green_adv_solar_generator", GREEN_ADV_SOLAR_GENERATOR);
                registerBlock("red_adv_solar_generator", RED_ADV_SOLAR_GENERATOR);
                registerBlock("black_adv_solar_generator", BLACK_ADV_SOLAR_GENERATOR);

                // Horizontal Advanced Solar Generator Color Variants
                registerBlock("white_horz_adv_solar_generator", WHITE_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("orange_horz_adv_solar_generator", ORANGE_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("magenta_horz_adv_solar_generator", MAGENTA_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("light_blue_horz_adv_solar_generator", LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("yellow_horz_adv_solar_generator", YELLOW_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("lime_horz_adv_solar_generator", LIME_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("pink_horz_adv_solar_generator", PINK_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("gray_horz_adv_solar_generator", GRAY_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("light_gray_horz_adv_solar_generator", LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("cyan_horz_adv_solar_generator", CYAN_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("purple_horz_adv_solar_generator", PURPLE_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("blue_horz_adv_solar_generator", BLUE_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("brown_horz_adv_solar_generator", BROWN_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("green_horz_adv_solar_generator", GREEN_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("red_horz_adv_solar_generator", RED_HORZ_ADV_SOLAR_GENERATOR);
                registerBlock("black_horz_adv_solar_generator", BLACK_HORZ_ADV_SOLAR_GENERATOR);
        }

        private static void registerBlock(String name, Block block) {
                Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(CreatePhotomovement.MOD_ID, name),
                                block);
        }
}
