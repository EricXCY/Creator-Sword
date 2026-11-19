package com.erix.creatorsword.block.sword_pedestal;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SwordPedestalDirectionSlot extends ValueBoxTransform.Sided {
    @Override protected boolean isSideActive(BlockState state, Direction side) {
        return side.getAxis() != state.getValue(SwordPedestalBlock.FACING).getAxis();
    }

    @Override public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        var facing = state.getValue(SwordPedestalBlock.FACING);
        return new Vec3(.5, .5, .5)
                .add(Vec3.atLowerCornerOf(getSide().getNormal()).scale(.501))
                .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(-5 / 16.0));
    }

    @Override protected Vec3 getSouthLocation() { return Vec3.ZERO; }
}
