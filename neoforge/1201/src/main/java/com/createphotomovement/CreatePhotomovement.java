package com.createphotomovement;

import com.createphotomovement.infrastructure.config.PMConfigs;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreatePhotomovement.MOD_ID)
public class CreatePhotomovement {
    public static final String MOD_ID = "createphotomovement";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreatePhotomovement() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AllBlocks.BLOCKS.register(modEventBus);
        AllItems.ITEMS.register(modEventBus);
        AllCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        AllBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);

        PMConfigs.register(ModLoadingContext.get());

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Create Photomovement initialized!");
    }
}
