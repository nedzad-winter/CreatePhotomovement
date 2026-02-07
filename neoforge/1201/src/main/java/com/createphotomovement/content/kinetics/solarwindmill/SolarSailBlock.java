package com.createphotomovement.content.kinetics.solarwindmill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.createphotomovement.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolarSailBlock extends WrenchableDirectionalBlock {

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
        super.createBlockStateDefinition(builder);
        builder.add(GLASS_COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(FACING, state.getValue(FACING).getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult ray) {
        ItemStack stack = player.getItemInHand(hand);

        // Check for dye -> sail dyeing
        if (stack.getItem() instanceof net.minecraft.world.item.DyeItem dyeItem) {
            DyeColor dyeColor = dyeItem.getDyeColor();

            if (!world.isClientSide) {
                world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f,
                        1.1f - world.random.nextFloat() * .2f);
                applyDye(state, world, pos, ray.getLocation(), dyeColor);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        // Standard placement logic (simplified for 1.20.1 without IPlacementHelper)
        if (!player.isShiftKeyDown() && player.mayBuild()) {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SolarSailBlock) {
                // Try to place extending the sail
                Direction facing = state.getValue(FACING);
                // We want to extend in the plane perpendicular to FACING
                // We can use the hit vector and direction to determine where to place
                // But without the helper, let's just do a simple check on the clicked face
                Direction clickedFace = ray.getDirection();
                if (clickedFace.getAxis() != facing.getAxis()) {
                    BlockPos targetPos = pos.relative(clickedFace);
                    if (world.getBlockState(targetPos).canBeReplaced()) {
                        BlockState newState = ((SolarSailBlock) blockItem.getBlock()).defaultBlockState()
                                .setValue(FACING, facing);

                        if (!world.isClientSide) {
                            world.setBlockAndUpdate(targetPos, newState);
                            SoundType soundType = newState.getBlock().getSoundType(newState);
                            world.playSound(null, targetPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                            if (!player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    public void applyDye(BlockState state, Level world, BlockPos pos, Vec3 hit, @Nullable DyeColor dyeColor) {
        if (dyeColor == null)
            return;

        BlockState newState = getSolarSailForColor(dyeColor)
                .defaultBlockState();
        newState = BlockHelper.copyProperties(state, newState);

        // Dye the block itself
        if (state.getBlock() != newState.getBlock()) {
            world.setBlockAndUpdate(pos, newState);
            // Don't return, we might want to propagate to neighbors even if self didn't
            // change (simplifies logic)
            // But usually we only start propagation if we successfully dyed self or if
            // already dyed and user clicks again?
            // 1.21 logic: checks if state != newState. If not equal, updates self and
            // returns.
            // Wait, 1.21 logic:
            // if (state != newState) { update(pos); return; }
            // This means simpler: click once -> dye self. Click again -> dye neighbors.
            return;
        }

        // Dye all adjacent (replacement for
        // IPlacementHelper.orderedByDistanceExceptAxis)
        List<Direction> directions = new ArrayList<>();
        Direction.Axis axis = state.getValue(FACING).getAxis();
        for (Direction d : Direction.values()) {
            if (d.getAxis() != axis) {
                directions.add(d);
            }
        }
        directions.sort(Comparator.comparingDouble(d -> pos.relative(d).distToCenterSqr(hit.x, hit.y, hit.z)));

        for (Direction d : directions) {
            BlockPos offset = pos.relative(d);
            BlockState adjacentState = world.getBlockState(offset);
            Block block = adjacentState.getBlock();
            if (!(block instanceof SolarSailBlock))
                continue;
            if (state.getValue(FACING) != adjacentState.getValue(FACING))
                continue;
            if (state.getBlock() == adjacentState.getBlock())
                continue;
            world.setBlockAndUpdate(offset, newState);
            return;
        }

        // Dye all connected sails (Recursive flood fill)
        List<BlockPos> frontier = new ArrayList<>();
        frontier.add(pos);
        Set<BlockPos> visited = new HashSet<>();
        int timeout = 100;
        while (!frontier.isEmpty()) {
            if (timeout-- < 0)
                break;

            BlockPos currentPos = frontier.remove(0);
            visited.add(currentPos);

            for (Direction d : Direction.values()) {
                if (d.getAxis() == state.getValue(FACING).getAxis())
                    continue;
                BlockPos offset = currentPos.relative(d);
                if (visited.contains(offset))
                    continue;
                BlockState adjacentState = world.getBlockState(offset);
                Block block = adjacentState.getBlock();
                if (!(block instanceof SolarSailBlock))
                    continue;
                if (adjacentState.getValue(FACING) != state.getValue(FACING))
                    continue;
                if (state.getBlock() != adjacentState.getBlock())
                    world.setBlockAndUpdate(offset, newState);
                frontier.add(offset);
                visited.add(offset);
            }
        }
    }

    private Block getSolarSailForColor(DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE -> AllBlocks.SOLAR_SAIL.get();
            case ORANGE -> AllBlocks.ORANGE_SOLAR_SAIL.get();
            case MAGENTA -> AllBlocks.MAGENTA_SOLAR_SAIL.get();
            case LIGHT_BLUE -> AllBlocks.LIGHT_BLUE_SOLAR_SAIL.get();
            case YELLOW -> AllBlocks.YELLOW_SOLAR_SAIL.get();
            case LIME -> AllBlocks.LIME_SOLAR_SAIL.get();
            case PINK -> AllBlocks.PINK_SOLAR_SAIL.get();
            case GRAY -> AllBlocks.GRAY_SOLAR_SAIL.get();
            case LIGHT_GRAY -> AllBlocks.LIGHT_GRAY_SOLAR_SAIL.get();
            case CYAN -> AllBlocks.CYAN_SOLAR_SAIL.get();
            case PURPLE -> AllBlocks.PURPLE_SOLAR_SAIL.get();
            case BLUE -> AllBlocks.BLUE_SOLAR_SAIL.get();
            case BROWN -> AllBlocks.BROWN_SOLAR_SAIL.get();
            case GREEN -> AllBlocks.GREEN_SOLAR_SAIL.get();
            case RED -> AllBlocks.RED_SOLAR_SAIL.get();
            case BLACK -> AllBlocks.BLACK_SOLAR_SAIL.get();
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AllShapes.SAIL.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
            Player player) {
        ItemStack pickBlock = super.getCloneItemStack(state, target, world, pos, player);
        if (pickBlock.isEmpty())
            return new ItemStack(AllBlocks.SOLAR_SAIL.get());
        return pickBlock;
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
