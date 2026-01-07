package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreatePhotomovement.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreatePhotomovementClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AllBlockEntityTypes.SOLAR_GENERATOR.get(), SolarGeneratorRenderer::new);
    }
}
