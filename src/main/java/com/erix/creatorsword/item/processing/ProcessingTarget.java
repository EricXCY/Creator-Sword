package com.erix.creatorsword.item.processing;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

record ProcessingTarget(ItemEntity item, BlockHitResult block) {
    static ProcessingTarget find(Player player) {
        var level = player.level();
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(player.blockInteractionRange()));
        BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE, player));
        double nearest = start.distanceToSqr(blockHit.getLocation());
        Entity selected = null;
        for (Entity entity : level.getEntities(player, new AABB(start, end).inflate(.3),
                e -> e.isAlive() && (e instanceof ItemEntity || e.isPickable()))) {
            var hit = entity.getBoundingBox().inflate(entity instanceof ItemEntity ? .15 : 0).clip(start, end);
            if (hit.isPresent() && start.distanceToSqr(hit.get()) < nearest) {
                nearest = start.distanceToSqr(hit.get());
                selected = entity;
            }
        }
        if (selected != null && !(selected instanceof ItemEntity)) return null;

        if (selected instanceof ItemEntity item)
            return level.mayInteract(player, item.blockPosition()) ? new ProcessingTarget(item, blockHit) : null;
        return blockHit.getType() == HitResult.Type.BLOCK && level.mayInteract(player, blockHit.getBlockPos())
                ? new ProcessingTarget(null, blockHit) : null;
    }
}
