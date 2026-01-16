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
                            // Original Solar Generator (clear glass)
                            entries.accept(AllItems.SOLAR_GENERATOR);
                            entries.accept(AllItems.HORIZONTAL_SOLAR_GENERATOR);
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
                            // Stained Glass Variants (Vertical Solar Generator)
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
                        }).build());
    }
}
