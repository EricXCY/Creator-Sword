package com.erix.creatorsword.item.flywheel_mace;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.config.CreatorSwordConfigs;
import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.erix.creatorsword.mixin.LivingEntityAttackStrengthAccessor;
import com.simibubi.create.AllSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = CreatorSword.MODID)
public final class FlywheelMaceDash {
    public static final int MIN_CHARGE_TICKS = 5;
    private static final int RELAY_BOOST_TICKS = 10;
    private static final Map<ServerPlayer, Dash> DASHES = new WeakHashMap<>();

    private FlywheelMaceDash() {}

    public static int chargeTicks() {
        return CreatorSwordConfigs.server() == null ? 80
                : CreatorSwordConfigs.server().flywheelMace.chargeTicks.get();
    }

    public static int dashTicks() {
        return CreatorSwordConfigs.server() == null ? 20
                : CreatorSwordConfigs.server().flywheelMace.dashTicks.get();
    }

    public static double baseDashSpeed() {
        return CreatorSwordConfigs.server() == null ? 0.7
                : CreatorSwordConfigs.server().flywheelMace.baseDashSpeed();
    }

    public static double energyDashSpeed() {
        return CreatorSwordConfigs.server() == null ? 0.9
                : CreatorSwordConfigs.server().flywheelMace.energyDashSpeed();
    }

    public static boolean isDashing(ServerPlayer player, ItemStack stack) {
        Dash dash = DASHES.get(player);
        return dash != null && dash.stack == stack;
    }

    public static void startWithEnergy(ServerPlayer player, ItemStack stack, int chargeTicks, float chargedEnergy) {
        if (chargeTicks < MIN_CHARGE_TICKS || player.getMainHandItem() != stack
                || player.getCooldowns().isOnCooldown(stack.getItem())
                || DASHES.containsKey(player)) return;

        float energy = Math.clamp(chargedEnergy, 0f, FlywheelMaceItem.getMaxEnergy(stack));
        stack.hurtAndBreak(3, player, EquipmentSlot.MAINHAND);
        if (stack.isEmpty()) return;
        Vec3 direction = player.getLookAngle().normalize();
        int duration = dashTicks();
        Dash dash = new Dash(stack, direction, player.position(), energy, duration, player.level().dimension());
        DASHES.put(player, dash);
        stack.remove(CSDataComponents.FLYWHEEL_ENERGY.get());
        player.getCooldowns().addCooldown(stack.getItem(), duration + 20);
        player.resetAttackStrengthTicker();
        propel(player, dash);
        AllSoundEvents.STEAM.play(player.level(), null, player.blockPosition(),
                0.7f + energy * 0.15f, 0.75f + energy * 0.2f);
    }

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Dash dash = DASHES.get(player);
        if (dash == null) return;
        Vec3 current = player.position();
        if (player.getMainHandItem() != dash.stack || player.level().dimension() != dash.dimension) {
            stop(player, dash);
            return;
        }

        hitPath(player, dash, current);
        dash.previous = current;
        dash.remaining--;
        dash.energy = dash.initialEnergy * dash.remaining / dash.duration;
        if (dash.remaining <= 0 || dash.stack.isEmpty()) {
            stop(player, dash);
            return;
        }
        player.serverLevel().sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.2, player.getZ(), 2, 0.2, 0.1, 0.2, 0.01);
        propel(player, dash);
        if (dash.relayBoostTicks > 0) dash.relayBoostTicks--;
    }

    private static void hitPath(ServerPlayer player, Dash dash, Vec3 current) {
        Vec3 start = dash.previous.add(0, player.getBbHeight() / 2, 0);
        Vec3 end = current.add(0, player.getBbHeight() / 2, 0);
        AABB search = player.getBoundingBox().expandTowards(dash.previous.subtract(current)).inflate(0.2);
        var targets = player.level().getEntitiesOfClass(LivingEntity.class, search,
                target -> canHit(player, target) && !dash.hit.contains(target.getUUID()));
        targets.sort(Comparator.comparingDouble(target -> target.position().distanceToSqr(dash.previous)));
        for (LivingEntity target : targets) {
            if (dash.stack.isEmpty() || player.getMainHandItem() != dash.stack) break;
            AABB contact = target.getBoundingBox().inflate(player.getBbWidth() / 2 + 0.2,
                    player.getBbHeight() / 2, player.getBbWidth() / 2 + 0.2);
            var intersection = contact.clip(start, end);
            if (!contact.contains(start) && !contact.contains(end) && intersection.isEmpty()) continue;
            Vec3 contactPoint = contact.contains(start) ? start : intersection.orElse(end);
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            if (player.level().clip(new ClipContext(contactPoint, targetCenter,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) continue;

            dash.hit.add(target.getUUID());
            double previousY = player.getDeltaMovement().y;
            float healthBefore = target.getHealth() + target.getAbsorptionAmount();
            smash(player, target, dash.energy);
            if (dash.relayLevel > 0 && target.getHealth() + target.getAbsorptionAmount() < healthBefore) {
                dash.relayBoostTicks = RELAY_BOOST_TICKS;
            }
            double afterHitY = player.getDeltaMovement().y;
            if (afterHitY != previousY && afterHitY > 0.011) {
                dash.verticalImpulse = Math.max(dash.verticalImpulse, afterHitY - 0.01);
            }
        }
    }

    private static boolean canHit(ServerPlayer player, LivingEntity target) {
        if (target == player || !target.isAlive() || target.isSpectator() || !target.isAttackable()
                || player.isAlliedTo(target)) return false;
        if (target instanceof TamableAnimal pet && pet.isOwnedBy(player)) return false;
        return !(target instanceof Player other) || player.canHarmPlayer(other);
    }

    private static void smash(ServerPlayer player, LivingEntity target, float energy) {
        float actualFallDistance = player.fallDistance;
        try {
            player.fallDistance = 1.5f + 4.5f * energy;
            ((LivingEntityAttackStrengthAccessor) player).creatorsword$setAttackStrengthTicker(Integer.MAX_VALUE / 2);
            player.attack(target);
        } finally {
            player.fallDistance = actualFallDistance;
            player.resetAttackStrengthTicker();
        }
    }

    private static void propel(ServerPlayer player, Dash dash) {
        double speed = baseDashSpeed() + energyDashSpeed() * dash.energy;
        if (dash.relayBoostTicks > 0) speed += 0.3 * dash.relayLevel;
        Vec3 velocity = dash.direction.scale(speed);
        player.setDeltaMovement(velocity.x, velocity.y + dash.verticalImpulse, velocity.z);
        dash.verticalImpulse *= 0.98;
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
    }

    private static void stop(ServerPlayer player, Dash dash) {
        DASHES.remove(player);
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Dash dash = DASHES.get(player);
            if (dash != null) stop(player, dash);
        }
    }

    private static final class Dash {
        private final ItemStack stack;
        private final Vec3 direction;
        private final float initialEnergy;
        private final int relayLevel;
        private int relayBoostTicks;
        private final ResourceKey<Level> dimension;
        private final Set<UUID> hit = new HashSet<>();
        private Vec3 previous;
        private float energy;
        private double verticalImpulse;
        private int remaining;
        private final int duration;

        private Dash(ItemStack stack, Vec3 direction, Vec3 previous, float energy, int duration,
                     ResourceKey<Level> dimension) {
            this.stack = stack;
            this.direction = direction;
            this.previous = previous;
            this.energy = energy;
            this.initialEnergy = energy;
            this.relayLevel = stack.getTagEnchantments().entrySet().stream()
                    .filter(entry -> entry.getKey().is(EnchantmentKeys.RELAY_IMPACT))
                    .mapToInt(entry -> Math.max(0, entry.getIntValue())).findFirst().orElse(0);
            this.remaining = duration;
            this.duration = duration;
            this.dimension = dimension;
        }
    }
}
