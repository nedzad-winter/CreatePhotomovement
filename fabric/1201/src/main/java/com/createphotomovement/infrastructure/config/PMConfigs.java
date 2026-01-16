package com.createphotomovement.infrastructure.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.createphotomovement.CreatePhotomovement;

import net.createmod.catnip.config.ConfigBase;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class PMConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static PMServer server;

    public static PMServer server() {
        return server;
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register() {
        server = register(PMServer::new, ModConfig.Type.SERVER);

        // Register configs using Forge Config API Port
        for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
            ForgeConfigRegistry.INSTANCE.register(CreatePhotomovement.MOD_ID, pair.getKey(),
                    pair.getValue().specification);
        }

        // Register config event handlers
        ModConfigEvents.loading(CreatePhotomovement.MOD_ID).register(config -> {
            for (ConfigBase c : CONFIGS.values()) {
                if (c.specification == config.getSpec()) {
                    c.onLoad();
                }
            }
        });

        ModConfigEvents.reloading(CreatePhotomovement.MOD_ID).register(config -> {
            for (ConfigBase c : CONFIGS.values()) {
                if (c.specification == config.getSpec()) {
                    c.onReload();
                }
            }
        });
    }
}
