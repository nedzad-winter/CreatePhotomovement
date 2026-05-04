package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorRenderer;
import com.createphotomovement.content.kinetics.solargenerator.HorizontalSolarGeneratorRenderer;
import com.createphotomovement.ponder.PhotomovementPonderPlugin;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;

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
        // Advanced generators use the same renderers (cast through generics)
        BlockEntityRenderers.register(AllBlockEntityTypes.ADV_SOLAR_GENERATOR,
                context -> new SolarGeneratorRenderer(context));
        BlockEntityRenderers.register(AllBlockEntityTypes.HORZ_ADV_SOLAR_GENERATOR,
                context -> new HorizontalSolarGeneratorRenderer(context));
        BlockEntityRenderers.register(AllBlockEntityTypes.SOLAR_WINDMILL_BEARING, BearingRenderer::new);
    }

    private static void registerRenderTypes() {
        // Default solar generators use cutout (clear glass)
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.SOLAR_GENERATOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.HORIZONTAL_SOLAR_GENERATOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.ADV_SOLAR_GENERATOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.HORZ_ADV_SOLAR_GENERATOR, RenderType.cutout());

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

        // Advanced solar generators use cutout (clear glass)
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.WHITE_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.ORANGE_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.MAGENTA_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_BLUE_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.YELLOW_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIME_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PINK_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GRAY_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_GRAY_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.CYAN_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PURPLE_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLUE_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BROWN_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GREEN_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.RED_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLACK_ADV_SOLAR_GENERATOR, RenderType.translucent());

        // Horizontal advanced solar generators use translucent
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.WHITE_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.ORANGE_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.MAGENTA_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.YELLOW_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIME_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PINK_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GRAY_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.CYAN_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.PURPLE_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLUE_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BROWN_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.GREEN_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.RED_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.BLACK_HORZ_ADV_SOLAR_GENERATOR, RenderType.translucent());
    }
}
