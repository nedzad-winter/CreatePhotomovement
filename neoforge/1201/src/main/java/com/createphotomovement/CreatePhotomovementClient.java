package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorRenderer;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorRenderer;
import com.createphotomovement.ponder.PhotomovementPonderPlugin;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

@Mod.EventBusSubscriber(modid = CreatePhotomovement.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
    @SuppressWarnings("unchecked")
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_GENERATOR.get(), SolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR.get(),
                HorizontalSolarGeneratorRenderer::new);
        event.registerBlockEntityRenderer(AllBlockEntityTypes.ADV_SOLAR_GENERATOR.get(),
                context -> (BlockEntityRenderer) new SolarGeneratorRenderer(context));
        event.registerBlockEntityRenderer(AllBlockEntityTypes.HORZ_ADV_SOLAR_GENERATOR.get(),
                context -> (BlockEntityRenderer) new HorizontalSolarGeneratorRenderer(context));
        event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_WINDMILL_BEARING.get(), BearingRenderer::new);
    }
}
