package com.erix.creatorsword.item.frogport_grapple;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface FrogportHookCompatProvider {
    @Nullable
    FrogportHookTarget tryResolve(Level level, BlockHitResult hit, Vec3 fallbackHitPos);

    @Nullable
    Vec3 tryResolveCurrentWorldPos(Level level, CompoundTag tag);
}