package com.erix.creatorsword.item.frogport_grapple;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.item.capture_box.CaptureBoxItem;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class FrogportGrappleItem extends Item implements CustomArmPoseItem {

    private static final String KEY_PHASE = "FrogTonguePhase";
    private static final String KEY_PROGRESS = "FrogTongueProgress";
    private static final String KEY_HOOKED = "FrogHooked";
    private static final String KEY_IS_ENTITY = "FrogHookIsEntity";
    private static final String KEY_ENTITY_ID = "FrogHookEntityId";
    private static final String KEY_HOOK_X = "FrogHookX";
    private static final String KEY_HOOK_Y = "FrogHookY";
    private static final String KEY_HOOK_Z = "FrogHookZ";
    private static final String KEY_PROGRESS_PREV = "FrogTongueProgressPrev";

    private static final ResourceLocation STAT_GRAPPLE_TRAVEL = FrogportGrappleTravelStat.ID;
    private static final int TRAVEL_TARGET = 100000;

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreatorSword.MODID);
    public static final DeferredHolder<Item, FrogportGrappleItem> FROGPORT_GRAPPLE =
            ITEMS.register("frogport_grapple",
                    () -> new FrogportGrappleItem(new Item.Properties().durability(250).stacksTo(1)));

    public FrogportGrappleItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new FrogportGrappleItemRender()));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public net.minecraft.client.model.HumanoidModel.ArmPose getArmPose(
            ItemStack stack,
            AbstractClientPlayer player,
            InteractionHand hand
    ) {
        return net.minecraft.client.model.HumanoidModel.ArmPose.ITEM;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 右键收回
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (tag.getBoolean(KEY_HOOKED)) {
            playRetractAndClear(level, player, stack);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        // 发射
        FrogportGrappleSounds.playExtend(level, player);

        double range = 48.0;
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = eye.add(look.scale(range));

        // 方块命中
        BlockHitResult blockHit = level.clip(new ClipContext(
                eye, end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        Vec3 endForEntity = (blockHit.getType() == HitResult.Type.BLOCK)
                ? blockHit.getLocation()
                : end;

        EntityHitResult entityHit = getEntityHit(level, player, eye, endForEntity);

        if (isEntityCloser(eye, blockHit, entityHit)) {
            if (tryHookEntity(level, player, hand, stack, entityHit)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
            return InteractionResultHolder.pass(stack);
        }

        // 否则抓方块
        if (blockHit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = blockHit.getBlockPos();
        if (level.isEmptyBlock(pos) || level.getBlockState(pos).getBlock() == Blocks.AIR) {
            return InteractionResultHolder.pass(stack);
        }

        FrogportGrappleSounds.playLatch(level, player);

        if (!level.isClientSide) {
            startBlockHook(stack, pos);
            damageOnHook(level, player, hand, stack, 1);
            awardTravelStatIfServer(player, eye, pos);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack,
                                               @NotNull ItemStack newStack,
                                               boolean slotChanged) {
        if (ItemStack.isSameItemSameComponents(oldStack, newStack)) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack,
                              @NotNull Level level,
                              @NotNull Entity entity,
                              int slot,
                              boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (!(entity instanceof Player player)) {
            return;
        }

        if (level.isClientSide) {
            tickTongueAnimation(stack);
            return;
        }

        boolean isInHand = selected || player.getOffhandItem() == stack;

        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        boolean hooked = tag.getBoolean(KEY_HOOKED);

        if (!isInHand) {
            if (hooked || tag.getInt(KEY_PHASE) != 0) {
                playRetractAndClear(level, player, stack);
            }
            return;
        }

        if (!hooked) {
            return;
        }

        boolean isEntityHook = tag.getBoolean(KEY_IS_ENTITY);

        if (isEntityHook) {
            pullEntityTowardsPlayer(stack, level, player, tag);
        } else {
            pullPlayerTowardsBlock(stack, level, player, tag);
        }
    }

    private static boolean isEntityCloser(Vec3 eye, BlockHitResult blockHit, @Nullable EntityHitResult entityHit) {
        if (entityHit == null) return false;
        if (blockHit.getType() != HitResult.Type.BLOCK) return true;
        return eye.distanceToSqr(entityHit.getLocation()) < eye.distanceToSqr(blockHit.getLocation());
    }

    private static boolean tryHookEntity(Level level, Player player, InteractionHand hand, ItemStack stack,
                                         @Nullable EntityHitResult entityHit) {
        if (entityHit == null) return false;

        LivingEntity living = resolveLiving(entityHit.getEntity());
        if (living == null) return false;

        if (!GrappleTargetCondition.canPullTarget(stack, living, level)) {
            return false;
        }

        FrogportGrappleSounds.playLatch(level, player);

        if (!level.isClientSide) {
            startEntityHook(stack, living);
            damageOnHook(level, player, hand, stack, 1);
        }

        return true;
    }

    @Nullable
    private static LivingEntity resolveLiving(Entity hit) {
        if (hit instanceof LivingEntity le) return le;

        if (hit instanceof EnderDragonPart part) {
            return part.parentMob;
        }

        Entity v = hit.getVehicle();
        if (v instanceof LivingEntity le) return le;

        Entity root = hit.getRootVehicle();
        if (root instanceof LivingEntity le) return le;

        for (Entity p : hit.getPassengers()) {
            if (p instanceof LivingEntity le) return le;
        }

        return null;
    }

    private static void awardTravelStatIfServer(Player player, Vec3 eye, BlockPos pos) {
        if (!(player instanceof net.minecraft.server.level.ServerPlayer sp)) return;

        Vec3 hookPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        int add = (int) Math.floor(eye.distanceTo(hookPos));
        if (add <= 0) return;

        try {
            var stat = Stats.CUSTOM.get(STAT_GRAPPLE_TRAVEL);
            sp.awardStat(stat, add);

            int total = sp.getStats().getValue(stat);
            if (total >= TRAVEL_TARGET) {
                CreatorSwordCriteriaTriggers.TRAVELING_FROG.get().trigger(sp);
            }
        } catch (Exception e) {
            CreatorSword.LOGGER.warn("frogport_grapple_travel stat failed", e);
        }
    }

    private static void startEntityHook(ItemStack stack, LivingEntity living) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            t.putBoolean(KEY_HOOKED, true);
            t.putInt(KEY_PHASE, 1);
            t.putFloat(KEY_PROGRESS, 0.0f);

            t.putBoolean(KEY_IS_ENTITY, true);
            t.putInt(KEY_ENTITY_ID, living.getId());

            t.putDouble(KEY_HOOK_X, living.getX());
            t.putDouble(KEY_HOOK_Y, living.getY() + living.getBbHeight() * 0.5);
            t.putDouble(KEY_HOOK_Z, living.getZ());
        });
    }

    private static void startBlockHook(ItemStack stack, BlockPos pos) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            t.putBoolean(KEY_HOOKED, true);
            t.putDouble(KEY_HOOK_X, pos.getX() + 0.5);
            t.putDouble(KEY_HOOK_Y, pos.getY() + 0.5);
            t.putDouble(KEY_HOOK_Z, pos.getZ() + 0.5);

            t.putInt(KEY_PHASE, 1);
            t.putFloat(KEY_PROGRESS, 0.0f);

            t.putBoolean(KEY_IS_ENTITY, false);
            t.remove(KEY_ENTITY_ID);
        });
    }

    private static void playRetractAndClear(Level level, Player player, ItemStack stack) {
        FrogportGrappleSounds.playRetract(level, player);
        if (!level.isClientSide) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                t.putBoolean(KEY_HOOKED, false);
                t.putInt(KEY_PHASE, 3);
            });
        }
    }

    private static void pullPlayerTowardsBlock(ItemStack stack,
                                               Level level,
                                               Player player,
                                               CompoundTag tag) {
        double hx = tag.getDouble(KEY_HOOK_X);
        double hy = tag.getDouble(KEY_HOOK_Y);
        double hz = tag.getDouble(KEY_HOOK_Z);

        Vec3 hookPos = new Vec3(hx, hy, hz);
        Vec3 playerPos = player.position();

        Vec3 toHook = hookPos.subtract(playerPos);
        double dist = toHook.length();

        if (dist < 1.5) {
            playRetractAndClear(level, player, stack);
            return;
        }

        applyPullMotion(player, toHook.normalize(), 0.6, 1.2);

        if (player.getDeltaMovement().y() > -0.2) {
            player.fallDistance = 0.0f;
        }
    }

    private static void pullEntityTowardsPlayer(ItemStack stack,
                                                Level level,
                                                Player player,
                                                CompoundTag tag) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }

        int entityId = tag.getInt(KEY_ENTITY_ID);
        if (entityId == 0) {
            playRetractAndClear(level, player, stack);
            return;
        }

        Entity hookedEntity = serverLevel.getEntity(entityId);
        if (!(hookedEntity instanceof LivingEntity target) || !hookedEntity.isAlive()) {
            playRetractAndClear(level, player, stack);
            return;
        }

        if (!GrappleTargetCondition.canPullTarget(stack, target, level)) {
            playRetractAndClear(level, player, stack);
            return;
        }

        Vec3 playerCenter = player.position().add(0, player.getBbHeight() * 0.5, 0);
        Vec3 targetCenter = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 toPlayer = playerCenter.subtract(targetCenter);
        AABB pBox = player.getBoundingBox().inflate(0.0);
        AABB tBox = target.getBoundingBox().inflate(0.0);

        if (pBox.intersects(tBox)) {
            if (tryCaptureWithBox(player, target)) {
                playRetractAndClear(level, player, stack);
                return;
            }
            playRetractAndClear(level, player, stack);
            return;
        }

        applyPullMotion(target, toPlayer.normalize(), 0.45, 1.0);
    }

    private static void applyPullMotion(Entity entity, Vec3 direction, double strength, double maxSpeed) {
        Vec3 current = entity.getDeltaMovement();
        Vec3 newDelta = current.add(direction.scale(strength));

        double maxSpeedSq = maxSpeed * maxSpeed;
        if (newDelta.lengthSqr() > maxSpeedSq) {
            newDelta = newDelta.normalize().scale(maxSpeed);
        }

        entity.setDeltaMovement(newDelta);
        entity.hurtMarked = true;
    }

    @Nullable
    private static EntityHitResult getEntityHit(Level level, Player player, Vec3 start, Vec3 end) {
        AABB box = player.getBoundingBox()
                .expandTowards(end.subtract(start))
                .inflate(0.5D);

        return ProjectileUtil.getEntityHitResult(
                level,
                player,
                start,
                end,
                box,
                entity -> entity.isPickable()
                        && !entity.isSpectator()
                        && !entity.is(player)
        );
    }

    private static void tickTongueAnimation(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();

        int phase = tag.getInt(KEY_PHASE);

        float prevProgress = tag.getFloat(KEY_PROGRESS);
        float progress = prevProgress;

        float step = 0.16f;

        switch (phase) {
            case 0 -> progress = 0f;

            case 1 -> { // 伸舌
                progress += step;
                if (progress >= 1f) {
                    progress = 1f;
                    boolean hooked = tag.getBoolean(KEY_HOOKED);
                    phase = hooked ? 2 : 3;
                }
            }

            case 2 -> progress = 1f; // 持续伸出

            case 3 -> { // 收舌
                progress -= step;
                if (progress <= 0f) {
                    progress = 0f;
                    phase = 0;
                }
            }
        }

        final int fPhase = phase;
        final float fPrev = prevProgress;
        final float fProgress = progress;

        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            t.putInt(KEY_PHASE, fPhase);
            t.putFloat(KEY_PROGRESS_PREV, fPrev);
            t.putFloat(KEY_PROGRESS, fProgress);
        });
    }

    private static boolean tryCaptureWithBox(Player player, LivingEntity target) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        if (off.getItem() instanceof CaptureBoxItem && !CaptureBoxItem.hasEntity(off)) {
            return CaptureBoxItem.captureEntity(off, target);
        }

        if (main.getItem() instanceof CaptureBoxItem && !CaptureBoxItem.hasEntity(main)) {
            return CaptureBoxItem.captureEntity(main, target);
        }

        return false;
    }

    private static EquipmentSlot slotForHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    private static void damageOnHook(Level level, Player player, InteractionHand hand, ItemStack stack, int amount) {
        if (level.isClientSide) return;
        if (player.getAbilities().instabuild) return;
        stack.hurtAndBreak(amount, player, slotForHand(hand));
    }
}
