package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.logic.CogwheelShieldStateManager;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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

public abstract class BaseCogwheelShieldEntity extends ThrowableItemProjectile {
    private static final String THROWN_SHIELD_TAG = "creatorsword_thrown_shield";

    private static final EntityDataAccessor<Float> SPEED =
            SynchedEntityData.defineId(BaseCogwheelShieldEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> RETURNING =
            SynchedEntityData.defineId(BaseCogwheelShieldEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<ItemStack> ITEM_STACK =
            SynchedEntityData.defineId(BaseCogwheelShieldEntity.class, EntityDataSerializers.ITEM_STACK);

    protected int lifetime = 0;

    public BaseCogwheelShieldEntity(EntityType<? extends BaseCogwheelShieldEntity> type, Level level) {
        super(type, level);
    }

    public BaseCogwheelShieldEntity(EntityType<? extends BaseCogwheelShieldEntity> type,
                                    Level level,
                                    LivingEntity owner,
                                    float speed,
                                    ItemStack stack) {
        super(type, owner, level);
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
        return getFallbackItem();
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack stack = this.getEntityData().get(ITEM_STACK);

        if (!stack.isEmpty())
            return stack;

        return new ItemStack(getFallbackItem());
    }

    protected abstract Item getFallbackItem();

    protected float getBaseDamage() {
        return 5.0f;
    }

    protected int getMaxLifetime() {
        return 100;
    }

    protected float getReturnSpeed() {
        return 1.2f;
    }

    protected float getPickupDistance() {
        return 1.0f;
    }

    protected float getSpeedDecayFactor() {
        return 0.8f;
    }

    protected int getSpeedDecayInterval() {
        return 5;
    }

    protected int getShieldDamageOnHit() {
        return 3;
    }

    protected float getDamage(float speed) {
        return Math.max(0f, getBaseDamage() * (speed / 256f));
    }

    public float getShieldSpeed() {
        return this.getEntityData().get(SPEED);
    }

    public boolean isReturning() {
        return this.getEntityData().get(RETURNING);
    }

    @Override
    public void tick() {
        boolean returning = isReturning();
        Entity owner = this.getOwner();

        if (owner instanceof Player player && returning) {
            moveTowardPlayer(player);
        }

        super.tick();

        if (this.level().isClientSide())
            return;

        if (this.getEntityData().get(ITEM_STACK).isEmpty()) {
            this.discard();
            return;
        }

        lifetime++;

        if (owner instanceof Player player) {
            double distanceToPlayer = distanceToPlayer(player);

            if (returning || lifetime >= getMaxLifetime()) {
                this.getEntityData().set(RETURNING, true);
                moveTowardPlayer(player);

                if (distanceToPlayer <= getPickupDistance()) {
                    returnToPlayer(player);
                    return;
                }
            }
        }

        slowDownOverTime();
    }

    private void moveTowardPlayer(Player player) {
        Vec3 playerPos = player.position().add(0, player.getBbHeight() * 0.5, 0);
        Vec3 toPlayer = playerPos.subtract(this.position());

        this.setNoGravity(true);

        if (toPlayer.length() > 0.05) {
            this.setDeltaMovement(toPlayer.normalize().scale(getReturnSpeed()));
        }
    }

    private double distanceToPlayer(Player player) {
        Vec3 playerPos = player.position().add(0, player.getEyeHeight() / 2, 0);
        return playerPos.subtract(this.position()).length();
    }

    private void returnToPlayer(Player player) {
        ItemStack stackData = this.getEntityData().get(ITEM_STACK);

        if (stackData.isEmpty()) {
            this.discard();
            return;
        }

        ItemStack stack = stackData.copy();
        float speed = getShieldSpeed();

        stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), speed);
        stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);

        if (player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
            player.setItemInHand(InteractionHand.OFF_HAND, stack);
        } else if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        } else {
            player.inventoryMenu.broadcastChanges();
        }

        if (stack.getItem() instanceof BaseCogwheelShieldItem shield) {
            shield.onReturned(player, stack, speed);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getPersistentData().remove(THROWN_SHIELD_TAG);
            CogwheelShieldStateManager.remove(serverPlayer);
        }

        this.discard();
    }

    private void slowDownOverTime() {
        float speed = getShieldSpeed();

        if (speed > 0 && lifetime % getSpeedDecayInterval() == 0) {
            float newSpeed = speed * getSpeedDecayFactor();

            if (newSpeed < 4f)
                newSpeed = 0f;

            this.getEntityData().set(SPEED, newSpeed);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (this.level().isClientSide())
            return;

        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) result;
            Entity target = entityHit.getEntity();

            if (target != this.getOwner()) {
                float damage = getDamage(getShieldSpeed());

                target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);

                if (damageShieldStack(getShieldDamageOnHit())) {
                    Entity owner = this.getOwner();

                    if (owner instanceof ServerPlayer serverPlayer) {
                        serverPlayer.getPersistentData().remove(THROWN_SHIELD_TAG);
                    }

                    this.discard();
                    return;
                }
            }
        }

        this.getEntityData().set(RETURNING, true);
        this.setNoGravity(true);
    }

    public float getRotationAngle(float partialTicks) {
        float speed = getShieldSpeed();
        float ageInSeconds = (this.tickCount + partialTicks) / 20f;

        return (speed * 6f * ageInSeconds) % 360f;
    }

    private boolean damageShieldStack(int amount) {
        Entity owner = this.getOwner();

        if (!(owner instanceof LivingEntity living))
            return false;

        ItemStack stack = this.getEntityData().get(ITEM_STACK);

        if (stack.isEmpty() || !stack.isDamageableItem())
            return false;

        ItemStack updated = stack.copy();
        updated.hurtAndBreak(amount, living, EquipmentSlot.OFFHAND);

        boolean broken = updated.isEmpty() || updated.getDamageValue() >= updated.getMaxDamage();

        this.getEntityData().set(ITEM_STACK, broken ? ItemStack.EMPTY : updated);

        return broken;
    }
}