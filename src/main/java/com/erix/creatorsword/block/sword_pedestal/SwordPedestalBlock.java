package com.erix.creatorsword.block.sword_pedestal;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.common.Tags;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import net.createmod.catnip.math.VoxelShaper;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;

public class SwordPedestalBlock extends DirectionalAxisKineticBlock implements ICogWheel, IBE<SwordPedestalBlockEntity>, ProperWaterloggedBlock {
    public static final BooleanProperty ROTATED = AXIS_ALONG_FIRST_COORDINATE;
    public static final BooleanProperty BOTTOM_SHAFT = EncasedCogwheelBlock.BOTTOM_SHAFT;
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 11, 16),
            Block.box(.95, 11, .95, 15.05, 13, 15.05));
    private static final VoxelShaper SHAPES = VoxelShaper.forDirectional(SHAPE, Direction.UP);
    public SwordPedestalBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(BOTTOM_SHAFT, false).setValue(ROTATED, false)
                .setValue(FACING, Direction.UP));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED, BOTTOM_SHAFT); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var facing = context.getClickedFace();
        var state = defaultBlockState().setValue(FACING, facing)
                .setValue(ROTATED, context.getHorizontalDirection().getAxis() == Direction.Axis.X);
        return withWater(withDetectedShaft(state, context.getLevel(), context.getClickedPos()), context);
    }

    public int getCapacityFor(ItemStack stack) {
        return SwordPedestalTags.capacityFor(stack);
    }

    public boolean hasSpeedControl() { return false; }

    public static BlockState withDetectedShaft(BlockState state, LevelReader level, BlockPos pos) {
        var facing = state.getValue(FACING);
        var behind = pos.relative(facing.getOpposite());
        var neighbour = level.getBlockState(behind);
        boolean shaft = neighbour.getBlock() instanceof IRotate rotating
                && rotating.hasShaftTowards(level, behind, neighbour, facing);
        return state.setValue(BOTTOM_SHAFT, shaft);
    }
    public static boolean modelRolled(BlockState state) {
        return state.getValue(ROTATED) == (state.getValue(FACING).getAxis() == Direction.Axis.X);
    }
    public static int modelXRotation(BlockState state) {
        var facing = state.getValue(FACING);
        return facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
    }
    public static int modelYRotation(BlockState state) {
        var facing = state.getValue(FACING);
        return facing.getAxis().isVertical() ? 0 : ((int) facing.toYRot() + 180) % 360;
    }
    @Override public @NotNull FluidState getFluidState(@NotNull BlockState state) { return fluidState(state); }
    @Override public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighbour, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighbourPos) {
        updateWater(level, state, pos);
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }
    @Override protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType type) { return false; }
    @Override public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) { return SHAPES.get(state.getValue(FACING)); }
    @Override public Direction.Axis getRotationAxis(BlockState state) { return state.getValue(FACING).getAxis(); }
    @Override public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) { return face == state.getValue(FACING).getOpposite() && state.getValue(BOTTOM_SHAFT); }
    @Override protected boolean areStatesKineticallyEquivalent(BlockState state, BlockState other) {
        if (other.is(this) && (state.getValue(BOTTOM_SHAFT) != other.getValue(BOTTOM_SHAFT)
                || state.getValue(FACING) != other.getValue(FACING))) return false;
        return super.areStatesKineticallyEquivalent(state, other);
    }
    @Override public Class<SwordPedestalBlockEntity> getBlockEntityClass() { return SwordPedestalBlockEntity.class; }
    @Override public BlockEntityType<? extends SwordPedestalBlockEntity> getBlockEntityType() { return SwordPedestalBlocks.SWORD_PEDESTAL_ENTITY.get(); }
    @Override protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                                 Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown() || !player.mayBuild()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (hit.getDirection() != state.getValue(FACING) && stack.is(Tags.Items.TOOLS_WRENCH))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var be = getBlockEntity(level, pos);
        if (be == null || !be.insert(player, hand)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull BlockHitResult hit) {
        if (!player.mayBuild() || !player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
        var be = getBlockEntity(level, pos);
        if (be == null || !be.hasHeldItem()) return InteractionResult.PASS;
        if (!level.isClientSide) player.getInventory().placeItemBackInInventory(be.extract());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        var player = context.getPlayer();
        if (player == null || !player.mayBuild()) return InteractionResult.PASS;
        if (context.getClickedFace() == state.getValue(FACING).getOpposite()) {
            var level = context.getLevel();
            if (!level.isClientSide) {
                KineticBlockEntity.switchToBlockState(level, context.getClickedPos(), state.cycle(BOTTOM_SHAFT));
                IWrenchable.playRotateSound(level, context.getClickedPos());
            }
            return InteractionResult.SUCCESS;
        }
        var be = getBlockEntity(context.getLevel(), context.getClickedPos());
        if (context.getClickedFace() == state.getValue(FACING) && be != null && !be.hasHeldItem()
                && be.insert(player, context.getHand()))
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        return super.onWrenched(state, context);
    }
    @Override public boolean hasAnalogOutputSignal(@NotNull BlockState state) { return true; }
    @Override public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        var be = getBlockEntity(level, pos);
        return be != null && be.hasHeldItem() ? 15 : 0;
    }
}
