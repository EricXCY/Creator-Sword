package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCogwheelShieldEntity extends Projectile implements ItemSupplier {
    private static final EntityDataAccessor<Float> ROTATION_SPEED =
            SynchedEntityData.defineId(BaseCogwheelShieldEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> RETURNING =
            SynchedEntityData.defineId(BaseCogwheelShieldEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<ItemStack> ITEM_STACK =
            SynchedEntityData.defineId(BaseCogwheelShieldEntity.class, EntityDataSerializers.ITEM_STACK);

    protected int lifetime = 0;
    private static final double FLIGHT_GRAVITY = 0.015;
    private static final double CONTACT_OFFSET = 0.05;
    private static final double AIR_DRAG = 0.99;
    private static final double WATER_DRAG = 0.8;
    private static final double RELAY_SPEED_PER_LEVEL = 0.1;
    private static final int RELAY_DURATION_TICKS = 10;
    private Vec3 boostVelocity = Vec3.ZERO;
    private int boostTicks;


    private int enchantmentLevel(ResourceKey<Enchantment> key) {
        return getItem().getTagEnchantments().entrySet().stream()
                .filter(entry -> entry.getKey().is(key))
                .mapToInt(entry -> entry.getIntValue()).findFirst().orElse(0);
    }

    @Override
    protected double getDefaultGravity() {
        return FLIGHT_GRAVITY;
    }

    public BaseCogwheelShieldEntity(EntityType<? extends BaseCogwheelShieldEntity> type, Level level) {
        super(type, level);
    }

    public BaseCogwheelShieldEntity(EntityType<? extends BaseCogwheelShieldEntity> type,
                                    Level level,
                                    LivingEntity owner,
                                    float speed,
                                    ItemStack stack) {
        super(type, level);
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.getEntityData().set(ROTATION_SPEED, speed);
        this.getEntityData().set(RETURNING, false);
        this.getEntityData().set(ITEM_STACK, stack.copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(ROTATION_SPEED, 0f);
        builder.define(RETURNING, false);
        builder.define(ITEM_STACK, ItemStack.EMPTY);
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
        return this.getEntityData().get(ROTATION_SPEED);
    }

    public boolean isReturning() {
        return this.getEntityData().get(RETURNING);
    }

    @Override
    public void tick() {
        super.tick(); // Projectile lifecycle only; all flight movement is owned here.
        if (level().isClientSide()) {
            // The server owns collisions and boost state. Extrapolate its velocity
            // between normal entity position/motion updates without applying hits.
            BlockHitResult obstacle = isReturning() ? null : traceFlightBlocks(getDeltaMovement());
            setPos(obstacle == null ? position().add(getDeltaMovement()) : obstacle.getLocation());
            updateRotation();
            return;
        }
        if (getEntityData().get(ITEM_STACK).isEmpty()) {
            discard();
            return;
        }
        if (boostTicks > 0 && --boostTicks == 0) clearBoost();
        lifetime++;
        if (lifetime >= getMaxLifetime()) beginReturn();

        if (isReturning()) tickReturn();
        else tickOutbound();
        if (isRemoved()) return;
        checkInsideBlocks();
        updateRotation();
        slowDownOverTime();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target != getOwner() && super.canHitEntity(target);
    }

    private void tickOutbound() {
        Vec3 end = position().add(getDeltaMovement());
        BlockHitResult obstacle = traceFlightBlocks(getDeltaMovement());
        if (obstacle != null) end = obstacle.getLocation();
        HitResult entityHit = ProjectileUtil.getEntityHitResult(level(), this, position(), end,
                getBoundingBox().expandTowards(getDeltaMovement()).inflate(1), this::canHitEntity);
        HitResult hit = entityHit != null ? entityHit : obstacle != null ? obstacle
                : BlockHitResult.miss(end, Direction.UP, BlockPos.containing(end));
        if (hit.getType() != HitResult.Type.MISS
                && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hit)) {
            hitTargetOrDeflectSelf(hit);
            if (isRemoved()) return;
            if (isReturning()) {
                tickReturn();
                return;
            }
            if (hit instanceof BlockHitResult) {
                applyFlightForces();
                return;
            }
        }
        // A creature can hide a wall in the original hit result. Always check
        // the actual displacement again, including any newly activated boost.
        BlockHitResult wall = traceFlightBlocks(getDeltaMovement());
        if (hit.getType() != HitResult.Type.BLOCK && wall != null
                && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, wall)) {
            onHit(wall);
            if (isReturning()) tickReturn();
            else applyFlightForces();
            return;
        }
        setPos(position().add(getDeltaMovement()));
        applyFlightForces();
    }

    /** Sweep the full shield bounds, stopping on the original flight line rather
     * than sliding along walls. Hit locations represent the safe entity position. */
    private BlockHitResult traceFlightBlocks(Vec3 movement) {
        Vec3 start = position();
        Vec3 end = start.add(movement);
        BlockHitResult closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        double halfWidth = getBbWidth() / 2.0;
        for (var shape : level().getBlockCollisions(this,
                getBoundingBox().expandTowards(movement).inflate(1.0e-7))) {
            for (AABB block : shape.toAabbs()) {
                // Minkowski expansion: sweep the shield's feet as a point
                // against blocks expanded by the shield's full dimensions.
                AABB expanded = new AABB(block.minX - halfWidth, block.minY - getBbHeight(),
                        block.minZ - halfWidth, block.maxX + halfWidth, block.maxY, block.maxZ + halfWidth);
                var intersection = expanded.clip(start, end);
                if (intersection.isEmpty()) continue;
                Vec3 contact = intersection.get();
                double distance = start.distanceToSqr(contact);
                if (distance >= closestDistance) continue;
                double[] distances = {Math.abs(contact.x - expanded.minX), Math.abs(contact.x - expanded.maxX),
                        Math.abs(contact.y - expanded.minY), Math.abs(contact.y - expanded.maxY),
                        Math.abs(contact.z - expanded.minZ), Math.abs(contact.z - expanded.maxZ)};
                Direction[] faces = {Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP,
                        Direction.NORTH, Direction.SOUTH};
                int nearest = 0;
                for (int i = 1; i < distances.length; i++) if (distances[i] < distances[nearest]) nearest = i;
                Direction face = faces[nearest];
                Vec3 center = contact.add(0, getBbHeight() / 2, 0);
                double extent = face.getAxis() == Direction.Axis.Y ? getBbHeight() / 2 : halfWidth;
                Vec3 blockPoint = center.subtract(Vec3.atLowerCornerOf(face.getNormal()).scale(extent + 1.0e-4));
                closest = new BlockHitResult(contact, face, BlockPos.containing(blockPoint), false);
                closestDistance = distance;
            }
        }
        return closest;
    }

    private void applyFlightForces() {
        Vec3 baseVelocity = getDeltaMovement().subtract(boostVelocity)
                .scale(isInWater() ? WATER_DRAG : AIR_DRAG);
        setDeltaMovement(baseVelocity);
        applyGravity();
        if (boostTicks > 0 && getDeltaMovement().lengthSqr() > 1.0e-12) {
            boostVelocity = getDeltaMovement().normalize().scale(boostVelocity.length());
        }
        setDeltaMovement(getDeltaMovement().add(boostVelocity));
    }

    private void clearBoost() {
        setDeltaMovement(getDeltaMovement().subtract(boostVelocity));
        boostVelocity = Vec3.ZERO;
        boostTicks = 0;
    }

    private void beginReturn() {
        if (isReturning()) return;
        clearBoost();
        getEntityData().set(RETURNING, true);
        setNoGravity(true);
        hasImpulse = true;
    }

    private void tickReturn() {
        if (!(getOwner() instanceof Player player) || !player.isAlive() || player.isRemoved()
                || player.level() != level()) {
            // No reachable owner: preserve the item instead of leaving a
            // stationary projectile forever after logout/death/dimension change.
            ItemStack dropped = getEntityData().get(ITEM_STACK).copy();
            dropped.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), getShieldSpeed());
            dropped.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
            dropped.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), true);
            spawnAtLocation(dropped);
            discard();
            return;
        }
        Vec3 toPlayer = player.position().add(0, player.getBbHeight() * 0.5, 0)
                .subtract(position());
        if (distanceToPlayer(player) <= getPickupDistance()) {
            returnToPlayer(player);
            return;
        }
        setDeltaMovement(toPlayer.normalize().scale(Math.min(getReturnSpeed(), toPlayer.length())));
        setPos(position().add(getDeltaMovement()));
        if (distanceToPlayer(player) <= getPickupDistance()) returnToPlayer(player);
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

        // Charging state belongs to the shield currently held by the player.
        // The thrown shield's state was cleared when it left the hand, so
        // returning it must not erase another shield's ongoing charge.
        this.discard();
    }

    private void slowDownOverTime() {
        float speed = getShieldSpeed();

        if (speed > 0 && lifetime % getSpeedDecayInterval() == 0) {
            float newSpeed = speed * getSpeedDecayFactor();

            if (newSpeed < 4f)
                newSpeed = 0f;

            this.getEntityData().set(ROTATION_SPEED, newSpeed);
        }
    }

    private static Vec3 reflect(Vec3 velocity, Vec3 normal) {
        double approach = velocity.dot(normal);
        return approach < 0 ? velocity.subtract(normal.scale(2.0 * approach)) : velocity;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (level().isClientSide() || isReturning()) return;
        if (result instanceof BlockHitResult blockHit) {
            Vec3 normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
            setPos(blockHit.getLocation().add(normal.scale(CONTACT_OFFSET)));
            if (enchantmentLevel(EnchantmentKeys.REBOUND) > 0) {
                // Reflect both components using the same surface, preserving
                // the temporary bonus independently of gravity and drag.
                boolean approaching = getDeltaMovement().dot(normal) < 0;
                setDeltaMovement(reflect(getDeltaMovement(), normal));
                if (approaching) {
                    boostVelocity = boostVelocity.subtract(normal.scale(2 * boostVelocity.dot(normal)));
                }
                hasImpulse = true;
            } else beginReturn();
            return;
        }
        if (result instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            if (target == getOwner()) return;
            boolean damaged = target.hurt(damageSources().thrown(this, getOwner()), getDamage(getShieldSpeed()));
            if (damaged && damageShieldStack(getShieldDamageOnHit())) {
                discard();
                return;
            }
            int relayLevel = enchantmentLevel(EnchantmentKeys.RELAY_IMPACT);
            if (target instanceof LivingEntity && relayLevel > 0) {
                int effectiveLevel = Math.min(relayLevel, 3);
                float restoredRotationSpeed = 64f * (1 << (effectiveLevel - 1));
                getEntityData().set(ROTATION_SPEED, Math.max(getShieldSpeed(), restoredRotationSpeed));
                Vec3 direction = getDeltaMovement().normalize();
                clearBoost();
                boostVelocity = direction.scale(RELAY_SPEED_PER_LEVEL * effectiveLevel);
                boostTicks = RELAY_DURATION_TICKS;
                setDeltaMovement(getDeltaMovement().add(boostVelocity));
                hasImpulse = true;
                return;
            }
        }
        beginReturn();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putFloat("ShieldSpeed", getShieldSpeed());
        tag.putBoolean("Returning", isReturning());
        tag.putInt("Lifetime", lifetime);
        tag.putInt("BoostTicks", boostTicks);
        tag.putDouble("BoostX", boostVelocity.x);
        tag.putDouble("BoostY", boostVelocity.y);
        tag.putDouble("BoostZ", boostVelocity.z);
        ItemStack stack = this.getEntityData().get(ITEM_STACK);

        if (!stack.isEmpty()) {
            tag.put("ShieldStack", stack.save(this.registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.getEntityData().set(ROTATION_SPEED, tag.getFloat("ShieldSpeed"));
        this.getEntityData().set(RETURNING, tag.getBoolean("Returning"));
        this.lifetime = tag.getInt("Lifetime");
        boostTicks = Math.max(0, tag.getInt("BoostTicks"));
        boostVelocity = boostTicks > 0
                ? new Vec3(tag.getDouble("BoostX"), tag.getDouble("BoostY"), tag.getDouble("BoostZ"))
                : Vec3.ZERO;
        // Old saves stored a multiplicative boost in Motion; remove it once.
        if (tag.contains("BoostMultiplier") && boostTicks > 0) {
            setDeltaMovement(getDeltaMovement().scale(1.0 / Math.max(1.0, tag.getDouble("BoostMultiplier"))));
            boostTicks = 0;
        }
        setNoGravity(isReturning());
        if (tag.contains("ShieldStack")) {
            ItemStack stack = ItemStack.parse(
                    this.registryAccess(),
                    tag.getCompound("ShieldStack")
            ).orElse(ItemStack.EMPTY);

            this.getEntityData().set(ITEM_STACK, stack);
        }
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