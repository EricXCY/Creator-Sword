package com.erix.creatorsword.item.cogwheel_shield;

import java.util.function.Consumer;

import com.erix.creatorsword.data.ShieldDataComponents;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
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
    public void tickClient(ItemStack stack, Player player, boolean keyDown) {
        long currentTick = player.level().getGameTime();
        long lastUpdateTick = stack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
        if (currentTick - lastUpdateTick < UPDATE_INTERVAL_TICKS)
            return;

        stack.set(ShieldDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), currentTick);

        float speed = getSpeed(stack);
        boolean charging = stack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        boolean decaying = stack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_DECAYING.get(), false);

        if (keyDown) {
            // 启动加速
            if (!charging) {
                charging = true;
                decaying = false;
            }

            float accelFactor = getAccelerationFactor(stack, player, currentTick);
            float nextSpeed = speed * accelFactor;
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
        stack.set(ShieldDataComponents.GEAR_SHIELD_CHARGING.get(), charging);
        stack.set(ShieldDataComponents.GEAR_SHIELD_DECAYING.get(), decaying);
    }

    public float getSpeed(ItemStack stack) {
        return stack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
    }

    public void setSpeed(ItemStack stack, float speed) {
        stack.set(ShieldDataComponents.GEAR_SHIELD_SPEED.get(), speed);
    }

    private float getAccelerationFactor(ItemStack stack, Player player, long tick) {
        int level = EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.PNEUMATIC_BOOST,
                stack
        );
        int k = level + 1;

        var tanks = BacktankUtil.getAllWithAir(player);
        boolean hasBacktankWithAir = !tanks.isEmpty();

        int hasTank = 0;
        if (hasBacktankWithAir) {
            boolean paid = consumeAirIfNeeded(stack, player, tick, k, tanks.getFirst());
            hasTank = paid ? 1 : 0;
        }

        return 1.05f + 0.1f * hasTank * k + 0.05f * level;
    }

    private boolean consumeAirIfNeeded(ItemStack stack, Player player, long tick, int enchantLevel, ItemStack tank) {
        long lastConsumeTick = stack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);
        if (tick - lastConsumeTick < AIR_CONSUME_INTERVAL_TICKS) return true;

        stack.set(ShieldDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), tick);

        int airCost = enchantLevel * 2;
        int air = BacktankUtil.getAir(tank);
        if (air < airCost) return false;
        BacktankUtil.consumeAir(player, tank, airCost);

        return true;
    }

    public static void resetNBT(ItemStack stack) {
        stack.set(ShieldDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(ShieldDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(ShieldDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
        stack.set(ShieldDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        stack.set(ShieldDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);
        stack.set(ShieldDataComponents.GEAR_SHIELD_ANGLE.get(), 0f);
    }

    private float getMaxSpeed(ItemStack stack, Player player) {
        int overdriveLevel = EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.OVERDRIVE,
                stack
        );
        return overdriveLevel > 0 ? OVERDRIVE_MAX_SPEED : NORMAL_MAX_SPEED;
    }
}