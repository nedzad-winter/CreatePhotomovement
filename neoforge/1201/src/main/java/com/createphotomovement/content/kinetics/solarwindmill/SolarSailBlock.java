package com.createphotomovement.content.kinetics.solarwindmill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.createphotomovement.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolarSailBlock extends Block implements IWrenchable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public enum GlassColor implements StringRepresentable {
        CLEAR("clear"),
        WHITE("white"),
        ORANGE("orange"),
        MAGENTA("magenta"),
        LIGHT_BLUE("light_blue"),
        YELLOW("yellow"),
        LIME("lime"),
        PINK("pink"),
        GRAY("gray"),
        LIGHT_GRAY("light_gray"),
        CYAN("cyan"),
        PURPLE("purple"),
        BLUE("blue"),
        BROWN("brown"),
        GREEN("green"),
        RED("red"),
        BLACK("black");

        private final String name;

        GlassColor(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public static GlassColor fromDyeColor(DyeColor dyeColor) {
            return switch (dyeColor) {
                case WHITE -> WHITE;
                case ORANGE -> ORANGE;
                case MAGENTA -> MAGENTA;
                case LIGHT_BLUE -> LIGHT_BLUE;
                case YELLOW -> YELLOW;
                case LIME -> LIME;
                case PINK -> PINK;
                case GRAY -> GRAY;
                case LIGHT_GRAY -> LIGHT_GRAY;
                case CYAN -> CYAN;
                case PURPLE -> PURPLE;
                case BLUE -> BLUE;
                case BROWN -> BROWN;
                case GREEN -> GREEN;
                case RED -> RED;
                case BLACK -> BLACK;
            };
        }
    }

    public static final EnumProperty<GlassColor> GLASS_COLOR = EnumProperty.create("glass_color", GlassColor.class);

    public static SolarSailBlock withCanvas(BlockBehaviour.Properties properties, DyeColor color) {
        return new SolarSailBlock(properties, color);
    }

    protected final DyeColor color;

    protected SolarSailBlock(BlockBehaviour.Properties properties, @Nullable DyeColor color) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(GLASS_COLOR, GlassColor.CLEAR));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GLASS_COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult ray) {
        ItemStack stack = player.getItemInHand(hand);

        // Check for dye -> sail dyeing
        if (stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor dyeColor = dyeItem.getDyeColor();

            if (!world.isClientSide) {
                world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f,
                        1.1f - world.random.nextFloat() * .2f);
                applyDye(state, world, pos, ray.getLocation(), dyeColor);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        // Placement Helper Logic
        if (Block.byItem(stack.getItem()) instanceof SolarSailBlock) {
            BlockPos targetPos = pos.relative(ray.getDirection());
            if (world.isClientSide)
                return InteractionResult.SUCCESS;

            if (world.getBlockState(targetPos).canBeReplaced()) {
                BlockState newState = Block.byItem(stack.getItem()).defaultBlockState()
                        .setValue(FACING, state.getValue(FACING));

                if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
                    // Check if there is a specific glass color on the item/block we are placing
                    // This simple check assumes default state or that we just want to place the
                    // item's default.
                    // The placement logic will naturally use the item's block.
                    // However, we want to copy the FACING from the clicked block.
                    // We already set FACING above.
                }

                world.setBlockAndUpdate(targetPos, newState);
                SoundType soundtype = newState.getSoundType(world, targetPos, player);
                world.playSound(null, targetPos, soundtype.getPlaceSound(), SoundSource.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // Visual model is 5-9/10 pixels from the base.
        // Bounds: 0-16, 5-9, 0-16 relative to facing
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case UP -> Block.box(0, 5, 0, 16, 9, 16);
            case DOWN -> Block.box(0, 7, 0, 16, 11, 16);
            case NORTH -> Block.box(0, 0, 7, 16, 16, 11);
            case SOUTH -> Block.box(0, 0, 5, 16, 16, 9);
            case WEST -> Block.box(7, 0, 0, 11, 16, 16);
            case EAST -> Block.box(5, 0, 0, 9, 16, 16);
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
            Player player) {
        return new ItemStack(AllBlocks.SOLAR_SAIL.get());
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // Soft landing on canvas sails
        super.fallOn(level, state, pos, entity, 0);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter world, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(world, entity);
        } else {
            this.bounce(entity);
        }
    }

    private void bounce(Entity entity) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(velocity.x, -velocity.y * 0.26F * d0, velocity.z);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    public DyeColor getColor() {
        return color;
    }
}
