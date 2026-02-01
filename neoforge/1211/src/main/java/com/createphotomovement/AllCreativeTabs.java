package com.createphotomovement;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllCreativeTabs {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
                        .create(Registries.CREATIVE_MODE_TAB, CreatePhotomovement.MOD_ID);

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(
                        "main",
                        () -> CreativeModeTab.builder()
                                        .title(Component.translatable("itemGroup.createphotomovement"))
                                        .icon(() -> AllItems.SOLAR_GENERATOR.get().getDefaultInstance())
                                        .displayItems((parameters, output) -> {
                                                // Original Solar Generator (clear glass)
                                                // Original Solar Generator (clear glass)
                                                output.accept(AllItems.SOLAR_GENERATOR.get());
                                                output.accept(AllItems.HORIZONTAL_SOLAR_GENERATOR.get());
                                                // Advanced Solar Generators
                                                output.accept(AllItems.ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.HORZ_ADV_SOLAR_GENERATOR.get());
                                                // Advanced Solar Generator Color Variants (Vertical) - Minecraft dye
                                                // order
                                                output.accept(AllItems.WHITE_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_GRAY_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GRAY_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLACK_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BROWN_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.RED_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.ORANGE_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.YELLOW_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIME_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GREEN_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.CYAN_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_BLUE_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLUE_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PURPLE_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.MAGENTA_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PINK_ADV_SOLAR_GENERATOR.get());
                                                // Advanced Solar Generator Color Variants (Horizontal) - Minecraft dye
                                                // order
                                                output.accept(AllItems.WHITE_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GRAY_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLACK_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BROWN_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.RED_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.ORANGE_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.YELLOW_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIME_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GREEN_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.CYAN_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLUE_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PURPLE_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.MAGENTA_HORZ_ADV_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PINK_HORZ_ADV_SOLAR_GENERATOR.get());

                                                // Horizontal Solar Generator Color Variants - Minecraft dye order
                                                output.accept(AllItems.WHITE_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GRAY_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLACK_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BROWN_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.RED_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.ORANGE_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.YELLOW_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIME_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GREEN_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.CYAN_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLUE_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PURPLE_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.MAGENTA_HORIZONTAL_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PINK_HORIZONTAL_SOLAR_GENERATOR.get());
                                                // Stained Glass Variants (Vertical Solar Generator) - Minecraft dye
                                                // order
                                                output.accept(AllItems.WHITE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_GRAY_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GRAY_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLACK_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BROWN_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.RED_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.ORANGE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.YELLOW_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIME_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GREEN_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.CYAN_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_BLUE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLUE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PURPLE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.MAGENTA_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PINK_SOLAR_GENERATOR.get());

                                                // Solar Sails - Minecraft dye order
                                                output.accept(AllItems.WHITE_SOLAR_SAIL.get());
                                                output.accept(AllItems.LIGHT_GRAY_SOLAR_SAIL.get());
                                                output.accept(AllItems.GRAY_SOLAR_SAIL.get());
                                                output.accept(AllItems.BLACK_SOLAR_SAIL.get());
                                                output.accept(AllItems.BROWN_SOLAR_SAIL.get());
                                                output.accept(AllItems.RED_SOLAR_SAIL.get());
                                                output.accept(AllItems.ORANGE_SOLAR_SAIL.get());
                                                output.accept(AllItems.YELLOW_SOLAR_SAIL.get());
                                                output.accept(AllItems.LIME_SOLAR_SAIL.get());
                                                output.accept(AllItems.GREEN_SOLAR_SAIL.get());
                                                output.accept(AllItems.CYAN_SOLAR_SAIL.get());
                                                output.accept(AllItems.LIGHT_BLUE_SOLAR_SAIL.get());
                                                output.accept(AllItems.BLUE_SOLAR_SAIL.get());
                                                output.accept(AllItems.PURPLE_SOLAR_SAIL.get());
                                                output.accept(AllItems.MAGENTA_SOLAR_SAIL.get());
                                                output.accept(AllItems.PINK_SOLAR_SAIL.get());

                                                // Solar Windmill Bearing
                                                output.accept(AllItems.SOLAR_WINDMILL_BEARING.get());
                                        }).build());
}
