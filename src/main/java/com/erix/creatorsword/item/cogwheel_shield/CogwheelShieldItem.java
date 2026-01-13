package com.erix.creatorsword.item.cogwheel_shield;

import java.util.function.Consumer;

import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class CogwheelShieldItem extends ShieldItem {
    private static final float MIN_SPEED = 8f;
    private static final float DECAY_RATE = 0.95f; // 衰减系数
    private static final int UPDATE_INTERVAL_TICKS = 2; // 每2tick更新一次
    private static final int AIR_CONSUME_INTERVAL_TICKS = 20; // 每秒消耗空气
    private static final float NORMAL_MAX_SPEED = 256f;
    private static final float OVERDRIVE_MAX_SPEED = 512f;

    public CogwheelShieldItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).durability(336));
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
    public int getEnchantmentValue() {
        return 20;
    }

    @OnlyIn(Dist.CLIENT)
    public void tickClient(ItemStack stack, Player player, boolean keyDown) {
        long currentTick = player.level().getGameTime();
        long lastUpdateTick = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
        if (currentTick - lastUpdateTick < UPDATE_INTERVAL_TICKS)
            return;

        stack.set(ModDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), currentTick);

        float speed = getSpeed(stack);
        boolean charging = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        boolean decaying = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);

        float accelFactor = getAccelerationFactor(stack, player);

        if (keyDown) {
            // 启动加速
            if (!charging) {
                charging = true;
                decaying = false;
            }
            float airBoost = consumeAirIfNeeded(stack, player, accelFactor, currentTick);
            // 使用当前速度计算增长
            float nextSpeed = speed * (accelFactor) * airBoost;
            speed = Math.min(Math.max(nextSpeed, MIN_SPEED), getMaxSpeed(stack, player));

        } else {
            // 衰减阶段
            if (charging) {
                charging = false;
                decaying = true;
            }

            if (decaying) {
                speed *= DECAY_RATE;
                if (speed < 1f) {
                    speed = 0f;
                    decaying = false;
                }
            }
        }

        setSpeed(stack, speed);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), charging);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), decaying);
    }

    public float getSpeed(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
    }

    public void setSpeed(ItemStack stack, float speed) {
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), speed);
    }

    private float consumeAirIfNeeded(ItemStack stack, Player player, float accelFactor, long tick) {
        long lastConsumeTick = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);
        if (tick - lastConsumeTick < AIR_CONSUME_INTERVAL_TICKS) return 1.0f;

        stack.set(ModDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), tick);

        var tanks = BacktankUtil.getAllWithAir(player);
        if (tanks.isEmpty())
            return 1.0f;

        int airCost = (int) (accelFactor * 2);
        if (BacktankUtil.getAir(tanks.getFirst()) < airCost)
            return 1.0f;

        // 消耗空气并触发提速
        BacktankUtil.consumeAir(player, tanks.getFirst(), airCost);
        return 1.1f;
    }

    private float getAccelerationFactor(ItemStack stack, Player player) {
        Registry<Enchantment> registry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> holder = registry.getHolder(EnchantmentKeys.PNEUMATIC_BOOST)
                .orElseThrow(() -> new IllegalStateException("Enchantment not found: " + EnchantmentKeys.PNEUMATIC_BOOST));

        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
        return (float) (1.1 + 0.05f * enchantLevel);
    }

    public static void resetNBT(ItemStack stack) {
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);
        stack.set(ModDataComponents.GEAR_SHIELD_ANGLE.get(), 0f);
    }

    private float getMaxSpeed(ItemStack stack, Player player) {
        Registry<Enchantment> registry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> holder = registry.getHolder(EnchantmentKeys.OVERDRIVE)
                .orElseThrow(() -> new IllegalStateException("Enchantment not found: " + EnchantmentKeys.OVERDRIVE));

        int overdriveLevel = EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
        return overdriveLevel > 0 ? OVERDRIVE_MAX_SPEED : NORMAL_MAX_SPEED;
    }
}