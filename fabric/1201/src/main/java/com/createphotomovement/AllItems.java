package com.createphotomovement;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class AllItems {
        // Main items
        public static final BlockItem SOLAR_GENERATOR = new BlockItem(AllBlocks.SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem HORIZONTAL_SOLAR_GENERATOR = new BlockItem(AllBlocks.HORIZONTAL_SOLAR_GENERATOR,
                        new Item.Properties());

        // Horizontal Solar Generator Color Variants
        public static final BlockItem WHITE_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.WHITE_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem ORANGE_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.ORANGE_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem MAGENTA_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.MAGENTA_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem YELLOW_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem LIME_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem PINK_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.PINK_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem GRAY_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.GRAY_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem CYAN_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.CYAN_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem PURPLE_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.PURPLE_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem BLUE_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem BROWN_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.BROWN_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem GREEN_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.GREEN_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem RED_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());
        public static final BlockItem BLACK_HORIZONTAL_SOLAR_GENERATOR = new BlockItem(
                        AllBlocks.BLACK_HORIZONTAL_SOLAR_GENERATOR, new Item.Properties());

        // Solar Generator Color Variants
        public static final BlockItem WHITE_SOLAR_GENERATOR = new BlockItem(AllBlocks.WHITE_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem ORANGE_SOLAR_GENERATOR = new BlockItem(AllBlocks.ORANGE_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem MAGENTA_SOLAR_GENERATOR = new BlockItem(AllBlocks.MAGENTA_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem LIGHT_BLUE_SOLAR_GENERATOR = new BlockItem(AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem YELLOW_SOLAR_GENERATOR = new BlockItem(AllBlocks.YELLOW_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem LIME_SOLAR_GENERATOR = new BlockItem(AllBlocks.LIME_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem PINK_SOLAR_GENERATOR = new BlockItem(AllBlocks.PINK_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem GRAY_SOLAR_GENERATOR = new BlockItem(AllBlocks.GRAY_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem LIGHT_GRAY_SOLAR_GENERATOR = new BlockItem(AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem CYAN_SOLAR_GENERATOR = new BlockItem(AllBlocks.CYAN_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem PURPLE_SOLAR_GENERATOR = new BlockItem(AllBlocks.PURPLE_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem BLUE_SOLAR_GENERATOR = new BlockItem(AllBlocks.BLUE_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem BROWN_SOLAR_GENERATOR = new BlockItem(AllBlocks.BROWN_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem GREEN_SOLAR_GENERATOR = new BlockItem(AllBlocks.GREEN_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem RED_SOLAR_GENERATOR = new BlockItem(AllBlocks.RED_SOLAR_GENERATOR,
                        new Item.Properties());
        public static final BlockItem BLACK_SOLAR_GENERATOR = new BlockItem(AllBlocks.BLACK_SOLAR_GENERATOR,
                        new Item.Properties());

        public static void register() {
                registerItem("solar_generator", SOLAR_GENERATOR);
                registerItem("horizontal_solar_generator", HORIZONTAL_SOLAR_GENERATOR);

                // Horizontal Solar Generator Color Variants
                registerItem("white_horizontal_solar_generator", WHITE_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("orange_horizontal_solar_generator", ORANGE_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("magenta_horizontal_solar_generator", MAGENTA_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("light_blue_horizontal_solar_generator", LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("yellow_horizontal_solar_generator", YELLOW_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("lime_horizontal_solar_generator", LIME_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("pink_horizontal_solar_generator", PINK_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("gray_horizontal_solar_generator", GRAY_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("light_gray_horizontal_solar_generator", LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("cyan_horizontal_solar_generator", CYAN_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("purple_horizontal_solar_generator", PURPLE_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("blue_horizontal_solar_generator", BLUE_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("brown_horizontal_solar_generator", BROWN_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("green_horizontal_solar_generator", GREEN_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("red_horizontal_solar_generator", RED_HORIZONTAL_SOLAR_GENERATOR);
                registerItem("black_horizontal_solar_generator", BLACK_HORIZONTAL_SOLAR_GENERATOR);

                // Solar Generator Color Variants
                registerItem("white_solar_generator", WHITE_SOLAR_GENERATOR);
                registerItem("orange_solar_generator", ORANGE_SOLAR_GENERATOR);
                registerItem("magenta_solar_generator", MAGENTA_SOLAR_GENERATOR);
                registerItem("light_blue_solar_generator", LIGHT_BLUE_SOLAR_GENERATOR);
                registerItem("yellow_solar_generator", YELLOW_SOLAR_GENERATOR);
                registerItem("lime_solar_generator", LIME_SOLAR_GENERATOR);
                registerItem("pink_solar_generator", PINK_SOLAR_GENERATOR);
                registerItem("gray_solar_generator", GRAY_SOLAR_GENERATOR);
                registerItem("light_gray_solar_generator", LIGHT_GRAY_SOLAR_GENERATOR);
                registerItem("cyan_solar_generator", CYAN_SOLAR_GENERATOR);
                registerItem("purple_solar_generator", PURPLE_SOLAR_GENERATOR);
                registerItem("blue_solar_generator", BLUE_SOLAR_GENERATOR);
                registerItem("brown_solar_generator", BROWN_SOLAR_GENERATOR);
                registerItem("green_solar_generator", GREEN_SOLAR_GENERATOR);
                registerItem("red_solar_generator", RED_SOLAR_GENERATOR);
                registerItem("black_solar_generator", BLACK_SOLAR_GENERATOR);
        }

        private static void registerItem(String name, Item item) {
                Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(CreatePhotomovement.MOD_ID, name), item);
        }
}
