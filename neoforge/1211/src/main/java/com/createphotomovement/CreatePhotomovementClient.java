package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorRenderer;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorRenderer;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import com.createphotomovement.ponder.PhotomovementPonderPlugin;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
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

        // Register Flywheel visualization for the solar windmill bearing — mirrors
        // Create's own registration for WindmillBearingBlockEntity. Without this,
        // BearingRenderer early-returns when Flywheel is active and the bearing
        // top + shaft never render.
        SimpleBlockEntityVisualizer
                .builder(AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get())
                .factory(BearingVisual::new)
                .apply();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_GENERATOR.get(), SolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR.get(),
                HorizontalSolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.ADV_SOLAR_GENERATOR.get(), SolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.HORZ_ADV_SOLAR_GENERATOR.get(),
                HorizontalSolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get(), BearingRenderer::new);
    }
}
