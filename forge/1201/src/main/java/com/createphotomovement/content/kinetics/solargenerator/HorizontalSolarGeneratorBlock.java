package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;
import com.createphotomovement.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.HashMap;

public class HorizontalSolarGeneratorBlock extends HorizontalKineticBlock
        implements IBE<HorizontalSolarGeneratorBlockEntity>, IWrenchable {

    // Map dye colors to their corresponding horizontal solar generator blocks
    private static final Map<DyeColor, java.util.function.Supplier<Block>> COLOR_TO_BLOCK = new HashMap<>();

    static {
        COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.ORANGE, () -> AllBlocks.ORANGE_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.MAGENTA, () -> AllBlocks.MAGENTA_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_BLUE, () -> AllBlocks.LIGHT_BLUE_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.YELLOW, () -> AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIME, () -> AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PINK, () -> AllBlocks.PINK_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GRAY, () -> AllBlocks.GRAY_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_GRAY, () -> AllBlocks.LIGHT_GRAY_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.CYAN, () -> AllBlocks.CYAN_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PURPLE, () -> AllBlocks.PURPLE_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLUE, () -> AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BROWN, () -> AllBlocks.BROWN_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GREEN, () -> AllBlocks.GREEN_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.RED, () -> AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLACK, () -> AllBlocks.BLACK_HORIZONTAL_SOLAR_GENERATOR.get());
    }

    public HorizontalSolarGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends HorizontalSolarGeneratorBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR.get();
    }

    @Override
    public Class<HorizontalSolarGeneratorBlockEntity> getBlockEntityClass() {
        return HorizontalSolarGeneratorBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING).getOpposite();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Panel faces the player by default
        // When shift is held, panel faces away from player
        Direction facing = context.getHorizontalDirection().getOpposite();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = context.getHorizontalDirection();
        }
        return defaultBlockState().setValue(HORIZONTAL_FACING, facing);
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        ItemStack stack = player.getItemInHand(hand);

        // Check if the player is holding a dye
        if (stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getDyeColor();
            Block targetBlock = COLOR_TO_BLOCK.get(color).get();

            // Don't change if already this color
            if (state.getBlock() == targetBlock) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide) {
                // Get the current facing to preserve orientation
                Direction currentFacing = state.getValue(HORIZONTAL_FACING);

                // Replace with the colored variant, preserving facing
                BlockState newState = targetBlock.defaultBlockState().setValue(HORIZONTAL_FACING, currentFacing);
                level.setBlock(pos, newState, 3);

                // Play dye sound
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

                // Consume dye if not in creative mode
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, BlockState oldState,
            boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        // Trigger immediate update after placement or state change (rotation)
        if (!level.isClientSide && state.getBlock() == this) {
            level.getBlockEntity(pos, AllBlockEntityTypes.HORIZONTAL_SOLAR_GENERATOR.get())
                    .ifPresent(HorizontalSolarGeneratorBlockEntity::forceUpdate);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}
