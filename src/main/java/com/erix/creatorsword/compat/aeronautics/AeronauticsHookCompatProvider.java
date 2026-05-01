package com.erix.creatorsword.compat.aeronautics;

import com.erix.creatorsword.item.frogport_grapple.FrogportHookCompatProvider;
import com.erix.creatorsword.item.frogport_grapple.FrogportHookTarget;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public final class AeronauticsHookCompatProvider implements FrogportHookCompatProvider {

    @Override
    @Nullable
    public FrogportHookTarget tryResolve(Level level, BlockHitResult hit, Vec3 fallbackHitPos) {
        Vec3 projected = Sable.HELPER.projectOutOfSubLevel(level, fallbackHitPos);

        if (projected.distanceToSqr(fallbackHitPos) < 1.0E-8) {
            return null;
        }

        SubLevel subLevel = Sable.HELPER.getContaining(level, fallbackHitPos);

        if (subLevel == null) {
            return FrogportHookTarget.world(projected);
        }

        UUID subLevelId = subLevel.getUniqueId();

        return FrogportHookTarget.sable(
                subLevelId,
                fallbackHitPos,
                projected
        );
    }

    @Override
    @Nullable
    public Vec3 tryResolveCurrentWorldPos(Level level, CompoundTag tag) {
        if (!tag.getBoolean(FrogportHookTarget.KEY_HOOK_DYNAMIC)) {
            return null;
        }

        if (!FrogportHookTarget.HOOK_KIND_SABLE.equals(tag.getString(FrogportHookTarget.KEY_HOOK_KIND))) {
            return null;
        }

        if (!tag.hasUUID(FrogportHookTarget.KEY_HOOK_SUBLEVEL_ID)) {
            return null;
        }

        SubLevelContainer container = SubLevelContainer.getContainer(level);

        if (container == null) {
            return null;
        }

        UUID subLevelId = tag.getUUID(FrogportHookTarget.KEY_HOOK_SUBLEVEL_ID);
        SubLevel subLevel = container.getSubLevel(subLevelId);

        if (subLevel == null || subLevel.isRemoved()) {
            return null;
        }

        Vec3 localPos = new Vec3(
                tag.getDouble(FrogportHookTarget.KEY_HOOK_LOCAL_X),
                tag.getDouble(FrogportHookTarget.KEY_HOOK_LOCAL_Y),
                tag.getDouble(FrogportHookTarget.KEY_HOOK_LOCAL_Z)
        );

        return subLevel.logicalPose().transformPosition(localPos);
    }
}