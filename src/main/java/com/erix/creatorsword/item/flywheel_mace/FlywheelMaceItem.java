package com.erix.creatorsword.item.flywheel_mace;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FlywheelMaceItem extends MaceItem {
    private static final int USE_DURATION = 72000;

    private static float getInertialStorageMultiplier(ItemStack stack) {
        for (var entry : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet()) {
            if (entry.getKey().is(EnchantmentKeys.INERTIAL_STORAGE)) {
                return 1f + 0.3f * Math.max(0, entry.getIntValue());
            }
        }
        return 1f;
    }

    public static float getMaxEnergy(ItemStack stack) {
        for (var entry : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet()) {
            if (entry.getKey().is(EnchantmentKeys.OVERDRIVE) && entry.getIntValue() > 0) return 2f * getInertialStorageMultiplier(stack);
        }
        return getInertialStorageMultiplier(stack);
    }

    public static int getPneumaticBoostLevel(ItemStack stack) {
        for (var entry : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet()) {
            if (entry.getKey().is(EnchantmentKeys.PNEUMATIC_BOOST)) {
                return Math.max(0, entry.getIntValue());
            }
        }
        return 0;
    }

    public static float getChargeSpeedMultiplier(ItemStack stack, boolean paidAir) {
        int level = getPneumaticBoostLevel(stack);
        float enchantmentBoost = 0.3f * level;
        float airBoost = paidAir ? 0.15f * (level + 1) : 0f;
        return (1f + enchantmentBoost + airBoost) * getInertialStorageMultiplier(stack);
    }

    public FlywheelMaceItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).durability(1500)
                .component(DataComponents.TOOL, MaceItem.createToolProperties())
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(
                                        BASE_ATTACK_DAMAGE_ID,
                                        9.0,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(
                                        BASE_ATTACK_SPEED_ID,
                                        -3.6,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.HEAVY_CORE);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        int durabilityCost = attacker instanceof ServerPlayer player
                && FlywheelMaceDash.isDashing(player, stack) ? 2 : 1;
        stack.hurtAndBreak(durabilityCost, attacker, EquipmentSlot.MAINHAND);
        if (canSmashAttack(attacker)) attacker.resetFallDistance();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) return;
        int ticks = USE_DURATION - remainingUseDuration;
        float oldEnergy = stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f);
        boolean paidAir = oldEnergy < getMaxEnergy(stack) && tryConsumeAir(player, stack);
        float maxEnergy = getMaxEnergy(stack);
        float energy = Math.min(oldEnergy
                + getChargeSpeedMultiplier(stack, paidAir) / FlywheelMaceDash.chargeTicks(),
                maxEnergy);
        if (maxEnergy - energy < 1.0e-5f) energy = maxEnergy;
        stack.set(CSDataComponents.FLYWHEEL_ENERGY.get(), energy);
        boolean reachedFullCharge = energy == maxEnergy
                && oldEnergy < energy;
        if (ticks > 0 && (reachedFullCharge || energy < maxEnergy && ticks % 5 == 0)) {
            level.playSound(null, player.blockPosition(), SoundEvents.TRIPWIRE_CLICK_ON,
                    player.getSoundSource(), 0.4f, 0.5f + energy / maxEnergy * 1.5f);
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof ServerPlayer player) {
            float chargedEnergy = stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f);
            stack.remove(CSDataComponents.FLYWHEEL_ENERGY.get());
            stack.remove(CSDataComponents.FLYWHEEL_LAST_AIR_TICK.get());
            FlywheelMaceDash.startWithEnergy(player, stack, USE_DURATION - timeLeft, chargedEnergy);
        }
    }

    private static boolean tryConsumeAir(ServerPlayer player, ItemStack stack) {
        int boostLevel = getPneumaticBoostLevel(stack);
        if (boostLevel <= 0) return false;
        var tanks = BacktankUtil.getAllWithAir(player);
        if (tanks.isEmpty()) return false;

        long tick = player.level().getGameTime();
        long lastAirTick = stack.getOrDefault(CSDataComponents.FLYWHEEL_LAST_AIR_TICK.get(), -1L);
        if (lastAirTick >= 0 && tick - lastAirTick < 5) return true;

        int airCost = (boostLevel + 1) * 2;
        ItemStack tank = tanks.getFirst();
        if (BacktankUtil.getAir(tank) < airCost) return false;
        BacktankUtil.consumeAir(player, tank, airCost);
        stack.set(CSDataComponents.FLYWHEEL_LAST_AIR_TICK.get(), tick);
        return true;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide) return;
        boolean active = entity instanceof ServerPlayer player
                && ((player.isUsingItem() && player.getUseItem() == stack)
                || FlywheelMaceDash.isDashing(player, stack));
        if (!active) {
            stack.remove(CSDataComponents.FLYWHEEL_ENERGY.get());
            stack.remove(CSDataComponents.FLYWHEEL_LAST_AIR_TICK.get());
        }
        float energy = stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f);
        if (energy > 0) {
            // Store accumulated extra rotation so charge changes never jump the flywheel's angle.
            float angle = stack.getOrDefault(CSDataComponents.FLYWHEEL_ROTATION.get(), 0f);
            stack.set(CSDataComponents.FLYWHEEL_ROTATION.get(), (angle + 24f * energy) % 360f);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f) > 0 || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float energy = stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f);
        return energy > 0 ? Math.round(13f * Math.min(energy / getMaxEnergy(stack), 1f)) : super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f) > 0
                ? 0xE9B44C : super.getBarColor(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        ItemStack oldWithoutAnimation = oldStack.copy();
        ItemStack newWithoutAnimation = newStack.copy();
        oldWithoutAnimation.remove(CSDataComponents.FLYWHEEL_ENERGY.get());
        oldWithoutAnimation.remove(CSDataComponents.FLYWHEEL_ROTATION.get());
        oldWithoutAnimation.remove(CSDataComponents.FLYWHEEL_LAST_AIR_TICK.get());
        newWithoutAnimation.remove(CSDataComponents.FLYWHEEL_ENERGY.get());
        newWithoutAnimation.remove(CSDataComponents.FLYWHEEL_ROTATION.get());
        newWithoutAnimation.remove(CSDataComponents.FLYWHEEL_LAST_AIR_TICK.get());
        return slotChanged || !ItemStack.matches(oldWithoutAnimation, newWithoutAnimation);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new FlywheelMaceRenderer()));
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);
    public static final DeferredItem<FlywheelMaceItem> FLYWHEEL_MACE =
            ITEMS.registerItem("flywheel_mace", FlywheelMaceItem::new);
}
