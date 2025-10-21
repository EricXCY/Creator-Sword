package com.erix.creatorsword.entity;

import com.erix.creatorsword.client.KeyBindings;
import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThrownCogwheelShield extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(ThrownCogwheelShield.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(ThrownCogwheelShield.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(ThrownCogwheelShield.class, EntityDataSerializers.ITEM_STACK);
    private static final float BASE_DAMAGE = 5.0f;
    private int lifetime = 0;
    private static final int MAX_LIFETIME = 100; // 5秒
    private static final float RETURN_SPEED = 1.2f;
    private static final float PICKUP_DISTANCE = 1.2f;
    private ItemStack cachedItemStack;

    public ThrownCogwheelShield(EntityType<? extends ThrownCogwheelShield> type, Level level) {
        super(type, level);
    }

    public ThrownCogwheelShield(Level level, LivingEntity shooter, float speed, ItemStack stack) {
        super(ModEntities.COGWHEEL_SHIELD_ENTITY.get(), shooter, level);
        this.getEntityData().set(SPEED, speed);
        this.getEntityData().set(RETURNING, false);
        this.getEntityData().set(ITEM_STACK, stack.copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPEED, 0f);
        builder.define(RETURNING, false);
        builder.define(ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return CogwheelShieldItems.COGWHEEL_SHIELD.get();
    }

    @Override
    public ItemStack getItem() {
        if (cachedItemStack == null) {
            ItemStack stack = this.getEntityData().get(ITEM_STACK);
            if (!stack.isEmpty()) {
                cachedItemStack = stack.copy();
            } else {
                cachedItemStack = new ItemStack(getDefaultItem());
            }
        }
        return cachedItemStack;
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            lifetime++;

            // 更新旋转角度
            float speed = this.getEntityData().get(SPEED);

            // 检查是否返回
            boolean isReturning = this.getEntityData().get(RETURNING);
            Entity owner = this.getOwner();
            if (owner instanceof Player player) {
                Vec3 playerPos = player.position().add(0, player.getEyeHeight() / 2, 0);
                Vec3 toPlayer = playerPos.subtract(this.position());
                double distanceToPlayer = toPlayer.length();

                if (isReturning || lifetime >= MAX_LIFETIME) {
                    // 返回逻辑
                    this.getEntityData().set(RETURNING, true);
                    this.setDeltaMovement(toPlayer.normalize().scale(RETURN_SPEED));

                    // 检查拾取
                    if (distanceToPlayer <= PICKUP_DISTANCE) {
                        ItemStack stack = getItem();
                        if (stack.isEmpty()) {
                            this.discard();
                            return;
                        }
                        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), speed);
                        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
                        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);

                        // 优先返回副手
                        if (player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                            player.setItemInHand(InteractionHand.OFF_HAND, stack);
                            if (KeyBindings.ROTATE_COGWHEEL.isDown()) {
                                stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
                                stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), true);
                            }
                        } else if (!player.getInventory().add(stack)) {
                            player.drop(stack, false);
                        } else {
                            player.inventoryMenu.broadcastChanges();
                        }
                        this.discard();
                    }
                }
            }
            // 减速逻辑
            if (speed > 0 && lifetime % 15 == 0) {
                speed /= 2f;
                if (speed < 8f) {
                    speed = 0f;
                }
                this.getEntityData().set(SPEED, speed);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide()) {
            if (result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) result;
                Entity target = entityHit.getEntity();
                if (target != this.getOwner()) {
                    float speed = this.getEntityData().get(SPEED);
                    float damage = BASE_DAMAGE * (speed / 256f);
                    damage = Math.max(0f, Math.min(damage, BASE_DAMAGE));
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
                }
            }
            // 碰撞后开始返回
            this.getEntityData().set(RETURNING, true);
        }
    }

    public float getRotationAngle() {
        float speed = this.getEntityData().get(SPEED);
        return (speed * this.tickCount / 20f) % 360f;
    }
}