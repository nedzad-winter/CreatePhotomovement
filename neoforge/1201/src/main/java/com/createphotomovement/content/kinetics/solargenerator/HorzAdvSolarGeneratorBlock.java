package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;
import com.createphotomovement.AllBlocks;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HorzAdvSolarGeneratorBlock extends HorizontalSolarGeneratorBlock {

    // Map dye colors to their corresponding horizontal advanced solar generator
    // blocks
    private static final Map<DyeColor, Supplier<Block>> COLOR_TO_BLOCK = new HashMap<>();

    static {
        COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> (Block) AllBlocks.WHITE_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.ORANGE, () -> (Block) AllBlocks.ORANGE_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.MAGENTA, () -> (Block) AllBlocks.MAGENTA_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_BLUE, () -> (Block) AllBlocks.LIGHT_BLUE_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.YELLOW, () -> (Block) AllBlocks.YELLOW_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIME, () -> (Block) AllBlocks.LIME_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PINK, () -> (Block) AllBlocks.PINK_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GRAY, () -> (Block) AllBlocks.GRAY_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_GRAY, () -> (Block) AllBlocks.LIGHT_GRAY_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.CYAN, () -> (Block) AllBlocks.CYAN_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PURPLE, () -> (Block) AllBlocks.PURPLE_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLUE, () -> (Block) AllBlocks.BLUE_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BROWN, () -> (Block) AllBlocks.BROWN_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GREEN, () -> (Block) AllBlocks.GREEN_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.RED, () -> (Block) AllBlocks.RED_HORZ_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLACK, () -> (Block) AllBlocks.BLACK_HORZ_ADV_SOLAR_GENERATOR.get());
    }

    public HorzAdvSolarGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends HorizontalSolarGeneratorBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.HORZ_ADV_SOLAR_GENERATOR.get();
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

        return InteractionResult.PASS;
    }
}
