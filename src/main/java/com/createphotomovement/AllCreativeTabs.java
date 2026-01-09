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
                                                output.accept(AllItems.SOLAR_GENERATOR.get());
                                                output.accept(AllItems.HORIZONTAL_SOLAR_GENERATOR.get());
                                                // Stained Glass Variants
                                                output.accept(AllItems.WHITE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.ORANGE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.MAGENTA_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_BLUE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.YELLOW_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIME_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PINK_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GRAY_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.LIGHT_GRAY_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.CYAN_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.PURPLE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLUE_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BROWN_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.GREEN_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.RED_SOLAR_GENERATOR.get());
                                                output.accept(AllItems.BLACK_SOLAR_GENERATOR.get());
                                        }).build());
}
