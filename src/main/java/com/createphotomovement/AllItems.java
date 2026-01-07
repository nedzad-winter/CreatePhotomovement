package com.createphotomovement;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class AllItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatePhotomovement.MOD_ID);

    public static final DeferredItem<BlockItem> SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem("solar_generator",
            AllBlocks.SOLAR_GENERATOR);

    // Stained Glass Variants
    public static final DeferredItem<BlockItem> WHITE_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "white_solar_generator",
            AllBlocks.WHITE_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> ORANGE_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "orange_solar_generator",
            AllBlocks.ORANGE_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> MAGENTA_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "magenta_solar_generator",
            AllBlocks.MAGENTA_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> LIGHT_BLUE_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "light_blue_solar_generator",
            AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> YELLOW_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "yellow_solar_generator",
            AllBlocks.YELLOW_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> LIME_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "lime_solar_generator",
            AllBlocks.LIME_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> PINK_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "pink_solar_generator",
            AllBlocks.PINK_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> GRAY_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "gray_solar_generator",
            AllBlocks.GRAY_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> LIGHT_GRAY_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "light_gray_solar_generator",
            AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> CYAN_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "cyan_solar_generator",
            AllBlocks.CYAN_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> PURPLE_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "purple_solar_generator",
            AllBlocks.PURPLE_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> BLUE_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "blue_solar_generator",
            AllBlocks.BLUE_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> BROWN_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "brown_solar_generator",
            AllBlocks.BROWN_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> GREEN_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "green_solar_generator",
            AllBlocks.GREEN_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> RED_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "red_solar_generator",
            AllBlocks.RED_SOLAR_GENERATOR);

    public static final DeferredItem<BlockItem> BLACK_SOLAR_GENERATOR = ITEMS.registerSimpleBlockItem(
            "black_solar_generator",
            AllBlocks.BLACK_SOLAR_GENERATOR);
}
