package com.erix.creatorsword.item.frogport_grapple;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class FrogportHookTargetResolver {
    private static final List<FrogportHookCompatProvider> PROVIDERS = new ArrayList<>();

    private FrogportHookTargetResolver() {
    }

    public static void registerProvider(FrogportHookCompatProvider provider) {
        PROVIDERS.add(provider);
    }

    public static FrogportHookTarget resolve(Level level, BlockHitResult hit, Vec3 fallbackHitPos) {
        for (FrogportHookCompatProvider provider : PROVIDERS) {
            FrogportHookTarget target = provider.tryResolve(level, hit, fallbackHitPos);
            if (target != null) {
                return target;
            }
        }

        return FrogportHookTarget.world(fallbackHitPos);
    }

    public static Vec3 resolveCurrentWorldPos(Level level, CompoundTag tag) {
        for (FrogportHookCompatProvider provider : PROVIDERS) {
            Vec3 pos = provider.tryResolveCurrentWorldPos(level, tag);
            if (pos != null) {
                return pos;
            }
        }

        return FrogportHookTarget.readFallbackWorldPos(tag);
    }
}