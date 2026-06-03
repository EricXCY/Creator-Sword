package com.erix.creatorsword.item.cogwheel_shield;

import java.util.function.Consumer;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class CogwheelShieldItem extends ShieldItem {
    public static final float MIN_SPEED = 8f;
    public static final float DECAY_RATE = 0.95f;
    public static final int UPDATE_INTERVAL_TICKS = 2;
    public static final int AIR_CONSUME_INTERVAL_TICKS = 20;

    public static final float NORMAL_MAX_SPEED = 256f;
    public static final float OVERDRIVE_MAX_SPEED = 512f;

    public CogwheelShieldItem(Properties properties) {
        super(properties.stacksTo(1).durability(336));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CogwheelShieldItemRenderer()));
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(AllBlocks.SHAFT.asItem());
    }

    @OnlyIn(Dist.CLIENT)
    public float getClientAccelerationFactor(ItemStack stack, Player player, long tick) {
        int level = EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.PNEUMATIC_BOOST,
                stack
        );

        int boostFactor = level + 1;

        boolean hasBacktankWithAir = !BacktankUtil.getAllWithAir(player).isEmpty();
        int hasTank = hasBacktankWithAir ? 1 : 0;

        return 1.05f + 0.1f * hasTank * boostFactor + 0.05f * level;
    }

    public void serverTickAcceleration(ItemStack stack, Player player, long tick) {
        if (player.level().isClientSide())
            return;

        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        int level = EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.PNEUMATIC_BOOST,
                stack
        );

        int boostFactor = level + 1;
        boolean paid = tryConsumeServerAir(player, tick, boostFactor);
        int hasTank = paid ? 1 : 0;
        float accelFactor = 1.05f + 0.1f * hasTank * boostFactor + 0.05f * level;
        float speed = CogwheelShieldChargingManager.getSpeed(serverPlayer);
        float nextSpeed = speed * accelFactor;

        speed = Math.min(
                Math.max(nextSpeed, MIN_SPEED),
                getMaxSpeed(stack, player)
        );

        CogwheelShieldChargingManager.setSpeed(serverPlayer, speed);
    }

    private boolean tryConsumeServerAir(Player player, long tick, int boostFactor) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return false;

        var tanks = BacktankUtil.getAllWithAir(player);
        if (tanks.isEmpty())
            return false;

        ItemStack tank = tanks.getFirst();
        long lastConsumeTick = CogwheelShieldChargingManager.getLastAirTick(serverPlayer);
        if (tick - lastConsumeTick < AIR_CONSUME_INTERVAL_TICKS)
            return true;

        int airCost = boostFactor * 2;
        if (BacktankUtil.getAir(tank) < airCost)
            return false;

        CogwheelShieldChargingManager.setLastAirTick(serverPlayer, tick);
        BacktankUtil.consumeAir(player, tank, airCost);

        return true;
    }

    public float getMaxSpeed(ItemStack stack, Player player) {
        int overdriveLevel = EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.OVERDRIVE,
                stack
        );

        return overdriveLevel > 0 ? OVERDRIVE_MAX_SPEED : NORMAL_MAX_SPEED;
    }

    public static void resetNBT(ItemStack stack) {
        stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(CSDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
        stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        stack.set(CSDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);
    }
}