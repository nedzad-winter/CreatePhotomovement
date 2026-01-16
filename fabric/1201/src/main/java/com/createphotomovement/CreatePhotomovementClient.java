package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorRenderer;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorRenderer;
import com.createphotomovement.ponder.PhotomovementPonderPlugin;

import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class CreatePhotomovementClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register Ponder plugin
        PonderIndex.addPlugin(new PhotomovementPonderPlugin());

        // Register block entity renderers
        registerRenderers();

        // Register render types for transparent blocks
        registerRenderTypes();
    }

    private static void registerRenderers() {
        BlockEntityRenderers.register(AllBlockEntityTypes.SOLAR_GENERATOR, SolarGeneratorRenderer::new);
        BlockEntityRenderers.register(AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR,
                HorizontalSolarGeneratorRenderer::new);
    }

    private static void registerRenderTypes() {
        // Default solar generators use cutout (clear glass)
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.SOLAR_GENERATOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.HORIZONTAL_SOLAR_GENERATOR, RenderType.cutout());

        // Colored solar generators use translucent (stained glass)
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.WHITE_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.ORANGE_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.MAGENTA_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.YELLOW_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIME_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PINK_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GRAY_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.CYAN_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PURPLE_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLUE_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BROWN_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GREEN_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.RED_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLACK_SOLAR_GENERATOR, RenderType.translucent());

        // Colored horizontal solar generators use translucent
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.WHITE_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.ORANGE_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.MAGENTA_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR,
                RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PINK_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GRAY_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR,
                RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.CYAN_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PURPLE_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BROWN_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GREEN_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLACK_HORIZONTAL_SOLAR_GENERATOR, RenderType.translucent());
    }
}
