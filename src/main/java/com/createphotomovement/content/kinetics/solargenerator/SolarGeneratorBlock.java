package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorBlock extends RotatedPillarKineticBlock implements IBE<SolarGeneratorBlockEntity>, IWrenchable {

    public SolarGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends SolarGeneratorBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.SOLAR_GENERATOR.get();
    }

    @Override
    public Class<SolarGeneratorBlockEntity> getBlockEntityClass() {
        return SolarGeneratorBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        // Shaft on two sides parallel to each other, defined by the axis
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Place with axis along the player's looking direction (or perpendicular? User said "side with shaft to player")
        // "wenn der block plaziert wird soll er so platziert werden dass das eine seite mit dem shaft zum spieler zeigt"
        // This means the shaft axis should be aligned with the player's line of sight, or rather, the face touching the player?
        // Usually, RotatedPillar places "axis" along the clicked face or player view.
        // If I gaze South and place it, I want the shaft to be North-South.
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
             return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getClockWise().getAxis());
        
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;

        withBlockEntityDo(context.getLevel(), context.getClickedPos(), SolarGeneratorBlockEntity::toggleReversed);
        // playRotateSound(context.getLevel(), context.getClickedPos());
        return InteractionResult.SUCCESS;
    }
    @Override
    public net.minecraft.core.Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }
}
