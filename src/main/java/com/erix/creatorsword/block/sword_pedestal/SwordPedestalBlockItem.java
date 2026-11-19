package com.erix.creatorsword.block.sword_pedestal;

import com.simibubi.create.AllBlocks;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SwordPedestalBlockItem extends BlockItem {
    private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new PlacementHelper());

    public SwordPedestalBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        var player = context.getPlayer();
        if (player == null || player.isShiftKeyDown() || !player.mayBuild()) return InteractionResult.PASS;
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        var helper = PlacementHelpers.get(PLACEMENT_HELPER_ID);
        if (!helper.matchesState(state)) return InteractionResult.PASS;
        var hit = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false);
        return helper.getOffset(player, level, state, pos, hit)
                .placeInWorld(level, this, player, context.getHand(), hit).result();
    }

    public static class PlacementHelper implements IPlacementHelper {
        @Override
        public @NotNull Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem() instanceof SwordPedestalBlockItem;
        }

        @Override
        public @NotNull Predicate<BlockState> getStatePredicate() {
            return state -> PlacementHelpers.getHelpersView().stream()
                    .anyMatch(helper -> helper != this && helper.matchesItem(AllBlocks.COGWHEEL.asStack())
                            && helper.matchesState(state));
        }

        @Override
        public @NotNull PlacementOffset getOffset(@NotNull Player player, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull BlockHitResult hit) {
            for (var helper : PlacementHelpers.getHelpersView()) {
                if (helper == this || !helper.matchesItem(AllBlocks.COGWHEEL.asStack()) || !helper.matchesState(state)) continue;
                var offset = helper.getOffset(player, level, state, pos, hit);
                if (!offset.isSuccessful()) continue;
                var cogState = offset.getTransform().apply(AllBlocks.COGWHEEL.getDefaultState());
                var axis = cogState.getValue(BlockStateProperties.AXIS);
                var facing = state.hasProperty(SwordPedestalBlock.FACING)
                        && state.getValue(SwordPedestalBlock.FACING).getAxis() == axis
                        ? state.getValue(SwordPedestalBlock.FACING)
                        : Direction.get(Direction.AxisDirection.POSITIVE, axis);
                var horizontal = player.getDirection().getOpposite();
                var target = offset.getBlockPos();
                return PlacementOffset.success(target, pedestal -> SwordPedestalBlock.withDetectedShaft(
                        pedestal.setValue(SwordPedestalBlock.FACING, facing)
                                .setValue(SwordPedestalBlock.ROTATED, horizontal.getAxis() == Direction.Axis.X), level, target));
            }
            return PlacementOffset.fail();
        }
    }
}
