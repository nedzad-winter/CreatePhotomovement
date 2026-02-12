package com.createphotomovement;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class AllCreativeTabs {
    public static CreativeModeTab MAIN_TAB;

    public static void register() {
        MAIN_TAB = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(CreatePhotomovement.MOD_ID, "main"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup.createphotomovement"))
                        .icon(() -> AllItems.SOLAR_GENERATOR.getDefaultInstance())
                        .displayItems((displayContext, entries) -> {
                            // Basic Solar Generators
                            entries.accept(AllItems.SOLAR_GENERATOR);
                            entries.accept(AllItems.HORIZONTAL_SOLAR_GENERATOR);

                            // Advanced Solar Generators (base)
                            entries.accept(AllItems.ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.HORZ_ADV_SOLAR_GENERATOR);

                            // Solar Generator Color Variants
                            entries.accept(AllItems.WHITE_SOLAR_GENERATOR);
                            entries.accept(AllItems.ORANGE_SOLAR_GENERATOR);
                            entries.accept(AllItems.MAGENTA_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_BLUE_SOLAR_GENERATOR);
                            entries.accept(AllItems.YELLOW_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIME_SOLAR_GENERATOR);
                            entries.accept(AllItems.PINK_SOLAR_GENERATOR);
                            entries.accept(AllItems.GRAY_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_GRAY_SOLAR_GENERATOR);
                            entries.accept(AllItems.CYAN_SOLAR_GENERATOR);
                            entries.accept(AllItems.PURPLE_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLUE_SOLAR_GENERATOR);
                            entries.accept(AllItems.BROWN_SOLAR_GENERATOR);
                            entries.accept(AllItems.GREEN_SOLAR_GENERATOR);
                            entries.accept(AllItems.RED_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLACK_SOLAR_GENERATOR);

                            // Horizontal Solar Generator Color Variants
                            entries.accept(AllItems.WHITE_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.ORANGE_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.MAGENTA_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.YELLOW_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIME_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.PINK_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.GRAY_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.CYAN_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.PURPLE_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLUE_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.BROWN_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.GREEN_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.RED_HORIZONTAL_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLACK_HORIZONTAL_SOLAR_GENERATOR);

                            // Advanced Solar Generator Color Variants
                            entries.accept(AllItems.WHITE_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.ORANGE_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.MAGENTA_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_BLUE_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.YELLOW_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIME_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.PINK_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.GRAY_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_GRAY_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.CYAN_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.PURPLE_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLUE_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.BROWN_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.GREEN_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.RED_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLACK_ADV_SOLAR_GENERATOR);

                            // Horizontal Advanced Solar Generator Color Variants
                            entries.accept(AllItems.WHITE_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.ORANGE_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.MAGENTA_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.YELLOW_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIME_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.PINK_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.GRAY_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.CYAN_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.PURPLE_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLUE_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.BROWN_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.GREEN_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.RED_HORZ_ADV_SOLAR_GENERATOR);
                            entries.accept(AllItems.BLACK_HORZ_ADV_SOLAR_GENERATOR);

                            // Solar Windmill Bearing & Sails
                            entries.accept(AllItems.SOLAR_WINDMILL_BEARING);
                            entries.accept(AllItems.SOLAR_SAIL);

                            // Solar Sail Color Variants
                            entries.accept(AllItems.ORANGE_SOLAR_SAIL);
                            entries.accept(AllItems.MAGENTA_SOLAR_SAIL);
                            entries.accept(AllItems.LIGHT_BLUE_SOLAR_SAIL);
                            entries.accept(AllItems.YELLOW_SOLAR_SAIL);
                            entries.accept(AllItems.LIME_SOLAR_SAIL);
                            entries.accept(AllItems.PINK_SOLAR_SAIL);
                            entries.accept(AllItems.GRAY_SOLAR_SAIL);
                            entries.accept(AllItems.LIGHT_GRAY_SOLAR_SAIL);
                            entries.accept(AllItems.CYAN_SOLAR_SAIL);
                            entries.accept(AllItems.PURPLE_SOLAR_SAIL);
                            entries.accept(AllItems.BLUE_SOLAR_SAIL);
                            entries.accept(AllItems.BROWN_SOLAR_SAIL);
                            entries.accept(AllItems.GREEN_SOLAR_SAIL);
                            entries.accept(AllItems.RED_SOLAR_SAIL);
                            entries.accept(AllItems.BLACK_SOLAR_SAIL);
                        }).build());
    }
}
