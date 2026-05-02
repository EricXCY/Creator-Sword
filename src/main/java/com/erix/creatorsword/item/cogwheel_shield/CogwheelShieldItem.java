package com.erix.creatorsword.item.cogwheel_shield;

import java.util.function.Consumer;

import com.erix.creatorsword.data.CSDataComponents;
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

    @OnlyIn(Dist.CLIENT)
    private boolean consumeAirIfNeeded(ItemStack stack, Player player, long tick, int enchantLevel, ItemStack tank) {
        long lastConsumeTick = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), 0L);

        if (tick - lastConsumeTick < AIR_CONSUME_INTERVAL_TICKS)
            return true;

        stack.set(CSDataComponents.GEAR_SHIELD_LAST_AIR_TICK.get(), tick);

        int airCost = enchantLevel * 2;
        int air = BacktankUtil.getAir(tank);

        if (air < airCost)
            return false;

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