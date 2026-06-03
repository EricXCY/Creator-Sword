package com.erix.creatorsword.entity;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldChargingManager;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

public class ThrownCogwheelShield extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Float> SPEED =
            SynchedEntityData.defineId(ThrownCogwheelShield.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> RETURNING =
            SynchedEntityData.defineId(ThrownCogwheelShield.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<ItemStack> ITEM_STACK =
            SynchedEntityData.defineId(ThrownCogwheelShield.class, EntityDataSerializers.ITEM_STACK);

    private static final float BASE_DAMAGE = 5.0f;
    private static final int MAX_LIFETIME = 100; // 5秒
    private static final float RETURN_SPEED = 1.2f;
    private static final float PICKUP_DISTANCE = 1.0f;

    private int lifetime = 0;

    public ThrownCogwheelShield(EntityType<? extends ThrownCogwheelShield> type, Level level) {
        super(type, level);
    }

    public ThrownCogwheelShield(Level level, LivingEntity shooter, float speed, ItemStack stack) {
        super(CSEntities.COGWHEEL_SHIELD_ENTITY.get(), shooter, level);
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
    public @NotNull ItemStack getItem() {
        ItemStack stack = this.getEntityData().get(ITEM_STACK);

        if (!stack.isEmpty())
            return stack;

        return new ItemStack(getDefaultItem());
    }

    @Override
    public void tick() {
        boolean isReturning = this.getEntityData().get(RETURNING);
        Entity owner = this.getOwner();

        if (owner instanceof Player player && isReturning) {
            Vec3 playerPos = player.position().add(0, player.getBbHeight() * 0.5, 0);
            Vec3 toPlayer = playerPos.subtract(this.position());
            double distanceToPlayer = toPlayer.length();

            this.setNoGravity(true);

            if (distanceToPlayer > 0.05) {
                this.setDeltaMovement(toPlayer.normalize().scale(RETURN_SPEED));
            }
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
            Vec3 playerPos = player.position().add(0, player.getEyeHeight() / 2, 0);
            Vec3 toPlayer = playerPos.subtract(this.position());
            double distanceToPlayer = toPlayer.length();

            if (isReturning || lifetime >= MAX_LIFETIME) {
                this.getEntityData().set(RETURNING, true);
                this.setNoGravity(true);

                if (distanceToPlayer > 0.05) {
                    this.setDeltaMovement(toPlayer.normalize().scale(RETURN_SPEED));
                }

                if (distanceToPlayer <= PICKUP_DISTANCE) {
                    returnToPlayer(player);
                }
            }
        }

        slowDownOverTime();
    }

    private void returnToPlayer(Player player) {
        ItemStack stackData = this.getEntityData().get(ITEM_STACK);

        if (stackData.isEmpty()) {
            this.discard();
            return;
        }

        ItemStack stack = stackData.copy();

        float speedNow = this.getEntityData().get(SPEED);
        stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), speedNow);
        stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);

        if (player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
            player.setItemInHand(InteractionHand.OFF_HAND, stack);
        } else if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        } else {
            player.inventoryMenu.broadcastChanges();
        }

        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            sp.getPersistentData().remove("creatorsword_thrown_shield");
            CogwheelShieldChargingManager.remove(sp);
        }

        this.discard();
    }

    private void slowDownOverTime() {
        float speed = this.getEntityData().get(SPEED);

        if (speed > 0 && lifetime % 5 == 0) {
            float newSpeed = speed * 0.8f;

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
                float speed = this.getEntityData().get(SPEED);
                float damage = BASE_DAMAGE * (speed / 256f);
                damage = Math.max(0f, damage);

                target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);

                if (damageShieldStack(3)) {
                    Entity owner = this.getOwner();

                    if (owner instanceof net.minecraft.server.level.ServerPlayer sp) {
                        sp.getPersistentData().remove("creatorsword_thrown_shield");
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
        float speed = this.getEntityData().get(SPEED);

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