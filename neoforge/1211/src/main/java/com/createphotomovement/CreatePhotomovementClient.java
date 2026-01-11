package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorRenderer;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorRenderer;
import com.createphotomovement.ponder.PhotomovementPonderPlugin;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreatePhotomovement.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreatePhotomovementClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register Ponder plugin
        PonderIndex.addPlugin(new PhotomovementPonderPlugin());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_GENERATOR.get(), SolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR.get(),
                HorizontalSolarGeneratorRenderer::new);
    }
}
