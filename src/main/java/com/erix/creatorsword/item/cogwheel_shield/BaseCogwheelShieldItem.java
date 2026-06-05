package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public abstract class BaseCogwheelShieldItem extends ShieldItem {
    public BaseCogwheelShieldItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ResourceLocation getHandleModelLocation() {
        return CogwheelShieldItems.COGWHEEL_SHIELD_HANDLE_MODEL;
    }

    public ResourceLocation getRotatingGearModelLocation() {
        return CogwheelShieldItems.COGWHEEL_SHIELD_GEAR_MODEL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new BaseCogwheelShieldRenderer(this)));
    }

    public float getMinSpeed(ItemStack stack) {
        return 8f;
    }

    public float getDecayRate(ItemStack stack) {
        return 0.95f;
    }

    public int getUpdateIntervalTicks(ItemStack stack) {
        return 2;
    }

    public int getAirConsumeIntervalTicks(ItemStack stack) {
        return 20;
    }

    public float getNormalMaxSpeed(ItemStack stack, Player player) {
        return 256f;
    }

    public float getOverdriveMaxSpeed(ItemStack stack, Player player) {
        return 512f;
    }

    public float getThrowSpeedThreshold(ItemStack stack, Player player) {
        return 64f;
    }

    public boolean canThrowFromHand(ItemStack stack, Player player, InteractionHand hand) {
        return hand == InteractionHand.OFF_HAND;
    }

    public boolean canConsumeBacktankAir(ItemStack stack, Player player) {
        return true;
    }

    public int getPneumaticBoostLevel(ItemStack stack, Player player) {
        return EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.PNEUMATIC_BOOST,
                stack
        );
    }

    public int getOverdriveLevel(ItemStack stack, Player player) {
        return EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.OVERDRIVE,
                stack
        );
    }

    public int getAirCost(ItemStack stack, Player player, int pneumaticBoostLevel) {
        return (pneumaticBoostLevel + 1) * 2;
    }

    public float getAccelerationFactor(ItemStack stack, Player player, boolean paidAir) {
        int level = getPneumaticBoostLevel(stack, player);
        int boostFactor = level + 1;
        int hasTank = paidAir ? 1 : 0;

        return 1.05f + 0.1f * hasTank * boostFactor + 0.05f * level;
    }

    public float getMaxSpeed(ItemStack stack, Player player) {
        return getOverdriveLevel(stack, player) > 0
                ? getOverdriveMaxSpeed(stack, player)
                : getNormalMaxSpeed(stack, player);
    }

    public void onServerSpeedChanged(ServerPlayer player, ItemStack stack, float oldSpeed, float newSpeed) {
    }

    public void onThrown(ServerPlayer player, ItemStack stack, float speed) {
    }

    public void onReturned(Player player, ItemStack stack, float speed) {
    }

    public abstract BaseCogwheelShieldEntity createThrownEntity(
            Level level,
            LivingEntity owner,
            float speed,
            ItemStack stack
    );

    public static void resetStackState(ItemStack stack) {
        stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(CSDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
        stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        stack.set(CSDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);
    }
}