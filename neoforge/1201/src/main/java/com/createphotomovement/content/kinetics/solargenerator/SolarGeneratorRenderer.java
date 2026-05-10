package com.createphotomovement.content.kinetics.solargenerator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorRenderer extends KineticBlockEntityRenderer<SolarGeneratorBlockEntity> {

    public SolarGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SolarGeneratorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
            int light, int overlay) {
        BlockState shaftState = shaft(getRotationAxisOf(be));
        SuperByteBuffer superBuffer = CachedBuffers.block(shaftState);
        if (be.canGeneratePower())
            standardKineticRotationTransform(superBuffer, be, light);
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
