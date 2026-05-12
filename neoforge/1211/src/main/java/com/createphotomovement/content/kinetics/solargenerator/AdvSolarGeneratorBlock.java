package com.createphotomovement.content.kinetics.solargenerator;

import com.createphotomovement.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AdvSolarGeneratorBlock extends SolarGeneratorBlock {

        // Map dye colors to their corresponding advanced solar generator blocks
        private static final java.util.Map<net.minecraft.world.item.DyeColor, java.util.function.Supplier<net.minecraft.world.level.block.Block>> COLOR_TO_BLOCK = new java.util.HashMap<>();

        static {
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.WHITE,
                                () -> com.createphotomovement.AllBlocks.WHITE_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.ORANGE,
                                () -> com.createphotomovement.AllBlocks.ORANGE_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.MAGENTA,
                                () -> com.createphotomovement.AllBlocks.MAGENTA_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.LIGHT_BLUE,
                                () -> com.createphotomovement.AllBlocks.LIGHT_BLUE_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.YELLOW,
                                () -> com.createphotomovement.AllBlocks.YELLOW_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.LIME,
                                () -> com.createphotomovement.AllBlocks.LIME_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.PINK,
                                () -> com.createphotomovement.AllBlocks.PINK_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.GRAY,
                                () -> com.createphotomovement.AllBlocks.GRAY_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.LIGHT_GRAY,
                                () -> com.createphotomovement.AllBlocks.LIGHT_GRAY_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.CYAN,
                                () -> com.createphotomovement.AllBlocks.CYAN_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.PURPLE,
                                () -> com.createphotomovement.AllBlocks.PURPLE_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.BLUE,
                                () -> com.createphotomovement.AllBlocks.BLUE_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.BROWN,
                                () -> com.createphotomovement.AllBlocks.BROWN_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.GREEN,
                                () -> com.createphotomovement.AllBlocks.GREEN_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.RED,
                                () -> com.createphotomovement.AllBlocks.RED_ADV_SOLAR_GENERATOR.get());
                COLOR_TO_BLOCK.put(net.minecraft.world.item.DyeColor.BLACK,
                                () -> com.createphotomovement.AllBlocks.BLACK_ADV_SOLAR_GENERATOR.get());
        }

        public AdvSolarGeneratorBlock(Properties properties) {
                super(properties);
        }

        @Override
        public BlockEntityType<? extends SolarGeneratorBlockEntity> getBlockEntityType() {
                return AllBlockEntityTypes.ADV_SOLAR_GENERATOR.get();
        }

        @Override
        protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                        Player player, InteractionHand hand, BlockHitResult hitResult) {

                // Check if the player is holding a dye
                if (stack.getItem() instanceof DyeItem dyeItem) {
                        net.minecraft.world.item.DyeColor color = dyeItem.getDyeColor();
                        // Skip modded dye colors that aren't in our color map to avoid NPEs / world corruption
                        if (!COLOR_TO_BLOCK.containsKey(color)) {
                                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                        }
                        net.minecraft.world.level.block.Block targetBlock = COLOR_TO_BLOCK.get(color).get();

                        // Don't change if already this color
                        if (state.getBlock() == targetBlock) {
                                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                        }

                        if (!level.isClientSide) {
                                // Get the current axis to preserve orientation
                                net.minecraft.core.Direction.Axis currentAxis = state.getValue(AXIS);

                                // Replace with the colored variant, preserving axis
                                BlockState newState = targetBlock.defaultBlockState().setValue(AXIS, currentAxis);
                                level.setBlock(pos, newState, 3);

                                // Play dye sound
                                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.DYE_USE,
                                                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                                // Consume dye if not in creative mode
                                if (!player.isCreative()) {
                                        stack.shrink(1);
                                }
                        }

                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }

                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

}
