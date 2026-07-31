package com.createphotomovement.content.kinetics.solargenerator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HorizontalSolarGeneratorRenderer extends KineticBlockEntityRenderer<HorizontalSolarGeneratorBlockEntity> {

    public HorizontalSolarGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(HorizontalSolarGeneratorBlockEntity be, float partialTicks, PoseStack ms,
            MultiBufferSource buffer,
            int light, int overlay) {
        // Get the facing direction - shaft comes out the OPPOSITE side (gearbox side)
        BlockState blockState = be.getBlockState();
        Direction facing = blockState.getValue(HorizontalSolarGeneratorBlock.HORIZONTAL_FACING);

        // Render half shaft only on the back side (gearbox side, opposite of facing)
        Direction shaftDirection = facing.getOpposite();
        SuperByteBuffer halfShaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState,
                shaftDirection);
        if (be.canGeneratePower())
            standardKineticRotationTransform(halfShaft, be, light);
        halfShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
