package com.createphotomovement;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            CreatePhotomovement.MOD_ID);

    public static final RegistryObject<BlockItem> SOLAR_GENERATOR = ITEMS.register("solar_generator",
            () -> new BlockItem(AllBlocks.SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    // Horizontal Solar Generator Color Variants
    public static final RegistryObject<BlockItem> WHITE_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "white_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.WHITE_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> ORANGE_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "orange_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.ORANGE_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> MAGENTA_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "magenta_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.MAGENTA_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "light_blue_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> YELLOW_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "yellow_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> LIME_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "lime_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> PINK_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "pink_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.PINK_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> GRAY_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "gray_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.GRAY_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "light_gray_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> CYAN_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "cyan_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.CYAN_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> PURPLE_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "purple_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.PURPLE_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BLUE_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "blue_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BROWN_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "brown_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.BROWN_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> GREEN_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "green_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.GREEN_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> RED_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "red_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BLACK_HORIZONTAL_SOLAR_GENERATOR = ITEMS.register(
            "black_horizontal_solar_generator",
            () -> new BlockItem(AllBlocks.BLACK_HORIZONTAL_SOLAR_GENERATOR.get(), new Item.Properties()));

    // Stained Glass Variants
    public static final RegistryObject<BlockItem> WHITE_SOLAR_GENERATOR = ITEMS.register(
            "white_solar_generator",
            () -> new BlockItem(AllBlocks.WHITE_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> ORANGE_SOLAR_GENERATOR = ITEMS.register(
            "orange_solar_generator",
            () -> new BlockItem(AllBlocks.ORANGE_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> MAGENTA_SOLAR_GENERATOR = ITEMS.register(
            "magenta_solar_generator",
            () -> new BlockItem(AllBlocks.MAGENTA_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> LIGHT_BLUE_SOLAR_GENERATOR = ITEMS.register(
            "light_blue_solar_generator",
            () -> new BlockItem(AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> YELLOW_SOLAR_GENERATOR = ITEMS.register(
            "yellow_solar_generator",
            () -> new BlockItem(AllBlocks.YELLOW_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> LIME_SOLAR_GENERATOR = ITEMS.register(
            "lime_solar_generator",
            () -> new BlockItem(AllBlocks.LIME_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> PINK_SOLAR_GENERATOR = ITEMS.register(
            "pink_solar_generator",
            () -> new BlockItem(AllBlocks.PINK_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> GRAY_SOLAR_GENERATOR = ITEMS.register(
            "gray_solar_generator",
            () -> new BlockItem(AllBlocks.GRAY_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> LIGHT_GRAY_SOLAR_GENERATOR = ITEMS.register(
            "light_gray_solar_generator",
            () -> new BlockItem(AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> CYAN_SOLAR_GENERATOR = ITEMS.register(
            "cyan_solar_generator",
            () -> new BlockItem(AllBlocks.CYAN_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> PURPLE_SOLAR_GENERATOR = ITEMS.register(
            "purple_solar_generator",
            () -> new BlockItem(AllBlocks.PURPLE_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BLUE_SOLAR_GENERATOR = ITEMS.register(
            "blue_solar_generator",
            () -> new BlockItem(AllBlocks.BLUE_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BROWN_SOLAR_GENERATOR = ITEMS.register(
            "brown_solar_generator",
            () -> new BlockItem(AllBlocks.BROWN_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> GREEN_SOLAR_GENERATOR = ITEMS.register(
            "green_solar_generator",
            () -> new BlockItem(AllBlocks.GREEN_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> RED_SOLAR_GENERATOR = ITEMS.register(
            "red_solar_generator",
            () -> new BlockItem(AllBlocks.RED_SOLAR_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BLACK_SOLAR_GENERATOR = ITEMS.register(
            "black_solar_generator",
            () -> new BlockItem(AllBlocks.BLACK_SOLAR_GENERATOR.get(), new Item.Properties()));
}
