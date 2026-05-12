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

public class AdvSolarGeneratorBlock extends SolarGeneratorBlock {

    // Map dye colors to their corresponding advanced solar generator blocks
    private static final Map<DyeColor, Supplier<Block>> COLOR_TO_BLOCK = new HashMap<>();

    static {
        COLOR_TO_BLOCK.put(DyeColor.WHITE, () -> AllBlocks.WHITE_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.ORANGE, () -> AllBlocks.ORANGE_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.MAGENTA, () -> AllBlocks.MAGENTA_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_BLUE, () -> AllBlocks.LIGHT_BLUE_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.YELLOW, () -> AllBlocks.YELLOW_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIME, () -> AllBlocks.LIME_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PINK, () -> AllBlocks.PINK_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GRAY, () -> AllBlocks.GRAY_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.LIGHT_GRAY, () -> AllBlocks.LIGHT_GRAY_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.CYAN, () -> AllBlocks.CYAN_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.PURPLE, () -> AllBlocks.PURPLE_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLUE, () -> AllBlocks.BLUE_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BROWN, () -> AllBlocks.BROWN_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.GREEN, () -> AllBlocks.GREEN_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.RED, () -> AllBlocks.RED_ADV_SOLAR_GENERATOR.get());
        COLOR_TO_BLOCK.put(DyeColor.BLACK, () -> AllBlocks.BLACK_ADV_SOLAR_GENERATOR.get());
    }

    public AdvSolarGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends SolarGeneratorBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ADV_SOLAR_GENERATOR.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        ItemStack stack = player.getItemInHand(hand);

        // Check if the player is holding a dye
        if (stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getDyeColor();
            // Skip modded dye colors that aren't in our color map to avoid NPEs / world corruption
            if (!COLOR_TO_BLOCK.containsKey(color)) {
                return InteractionResult.PASS;
            }
            Block targetBlock = COLOR_TO_BLOCK.get(color).get();

            // Don't change if already this color
            if (state.getBlock() == targetBlock) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide) {
                // Get the current axis to preserve orientation
                Direction.Axis currentAxis = state.getValue(AXIS);

                // Replace with the colored variant, preserving axis
                BlockState newState = targetBlock.defaultBlockState().setValue(AXIS, currentAxis);
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
