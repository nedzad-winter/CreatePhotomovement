package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;
import com.createphotomovement.AllBlocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
import java.util.HashMap;

public class SolarGeneratorBlock extends RotatedPillarKineticBlock
        implements IBE<SolarGeneratorBlockEntity>, IWrenchable {

    // Map dye colors to their corresponding solar generator blocks
    private static final Map<DyeColor, java.util.function.Supplier<Block>> COLOR_TO_BLOCK = new HashMap<>();

    static {
        COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.ORANGE, () -> AllBlocks.ORANGE_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.MAGENTA, () -> AllBlocks.MAGENTA_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_BLUE, () -> AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.YELLOW, () -> AllBlocks.YELLOW_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIME, () -> AllBlocks.LIME_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PINK, () -> AllBlocks.PINK_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GRAY, () -> AllBlocks.GRAY_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_GRAY, () -> AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.CYAN, () -> AllBlocks.CYAN_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PURPLE, () -> AllBlocks.PURPLE_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLUE, () -> AllBlocks.BLUE_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BROWN, () -> AllBlocks.BROWN_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GREEN, () -> AllBlocks.GREEN_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.RED, () -> AllBlocks.RED_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLACK, () -> AllBlocks.BLACK_SOLAR_GENERATOR.get());
    }

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
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getClockWise().getAxis());

        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        // Check if the player is holding a dye
        if (stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getDyeColor();
            Block targetBlock = COLOR_TO_BLOCK.get(color).get();

            // Don't change if already this color
            if (state.getBlock() == targetBlock) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!level.isClientSide) {
                // Get the current axis to preserve orientation
                Direction.Axis currentAxis = state.getValue(AXIS);

                // Get block entity data to preserve (like reversed state)
                boolean wasReversed = false;
                if (level.getBlockEntity(pos) instanceof SolarGeneratorBlockEntity be) {
                    wasReversed = be.isReversed();
                }

                // Replace with the colored variant, preserving axis
                BlockState newState = targetBlock.defaultBlockState().setValue(AXIS, currentAxis);
                level.setBlock(pos, newState, 3);

                // Restore block entity state
                if (level.getBlockEntity(pos) instanceof SolarGeneratorBlockEntity be) {
                    if (wasReversed) {
                        be.toggleReversed();
                    }
                }

                // Play dye sound
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

                // Consume dye if not in creative mode
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;

        withBlockEntityDo(context.getLevel(), context.getClickedPos(), SolarGeneratorBlockEntity::toggleReversed);
        return InteractionResult.SUCCESS;
    }

    @Override
    public net.minecraft.core.Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }
}
