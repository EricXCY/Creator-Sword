package com.erix.creatorsword.item.frogport_grapple;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public record FrogportHookTarget(
        String kind,
        boolean dynamic,
        @Nullable UUID subLevelId,
        Vec3 localPos,
        Vec3 worldPos
) {
    public static final String KEY_HOOK_X = "FrogHookX";
    public static final String KEY_HOOK_Y = "FrogHookY";
    public static final String KEY_HOOK_Z = "FrogHookZ";

    public static final String KEY_HOOK_DYNAMIC = "FrogHookDynamic";
    public static final String KEY_HOOK_KIND = "FrogHookKind";

    public static final String HOOK_KIND_WORLD = "world";
    public static final String HOOK_KIND_SABLE = "sable";

    public static final String KEY_HOOK_SUBLEVEL_ID = "FrogHookSubLevelId";
    public static final String KEY_HOOK_LOCAL_X = "FrogHookLocalX";
    public static final String KEY_HOOK_LOCAL_Y = "FrogHookLocalY";
    public static final String KEY_HOOK_LOCAL_Z = "FrogHookLocalZ";

    public static FrogportHookTarget world(Vec3 worldPos) {
        return new FrogportHookTarget(
                HOOK_KIND_WORLD,
                false,
                null,
                Vec3.ZERO,
                worldPos
        );
    }

    public static FrogportHookTarget sable(UUID subLevelId, Vec3 localPos, Vec3 worldPos) {
        return new FrogportHookTarget(
                HOOK_KIND_SABLE,
                true,
                subLevelId,
                localPos,
                worldPos
        );
    }

    public static Vec3 readFallbackWorldPos(CompoundTag tag) {
        return new Vec3(
                tag.getDouble(KEY_HOOK_X),
                tag.getDouble(KEY_HOOK_Y),
                tag.getDouble(KEY_HOOK_Z)
        );
    }
}