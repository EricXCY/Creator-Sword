package com.erix.creatorsword.item.cogwheel_shield.logic;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.BaseCogwheelShieldItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = CreatorSword.MODID)
public final class CogwheelShieldTicker {
    private CogwheelShieldTicker() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long tick = event.getServer().getTickCount();

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            CogwheelShieldStateManager.State state = CogwheelShieldStateManager.get(player);

            if (state == null)
                continue;

            ItemStack stack = CogwheelShieldUtil.getHeldCogwheelShield(player);

            if (!(stack.getItem() instanceof BaseCogwheelShieldItem shield)) {
                CogwheelShieldStateManager.remove(player);
                continue;
            }

            if (tick % shield.getUpdateIntervalTicks(stack) != 0)
                continue;

            if (state.charging) {
                tickCharging(player, stack, shield, state, tick);
            } else if (state.decaying) {
                tickDecaying(player, stack, shield, state, tick);
            }
        }
    }

    private static void tickCharging(ServerPlayer player, ItemStack stack,
                                     BaseCogwheelShieldItem shield,
                                     CogwheelShieldStateManager.State state,
                                     long tick) {
        float oldSpeed = state.speed;

        boolean paidAir = tryConsumeAir(player, stack, shield, state, tick);
        float accelerationFactor = shield.getAccelerationFactor(stack, player, paidAir);
        float nextSpeed = state.speed * accelerationFactor;

        state.speed = Math.min(
                Math.max(nextSpeed, shield.getMinSpeed(stack)),
                shield.getMaxSpeed(stack, player)
        );

        shield.onServerSpeedChanged(player, stack, oldSpeed, state.speed);
    }

    private static void tickDecaying(ServerPlayer player, ItemStack stack,
                                     BaseCogwheelShieldItem shield,
                                     CogwheelShieldStateManager.State state,
                                     long tick) {
        float oldSpeed = state.speed;

        state.speed *= shield.getDecayRate(stack);

        if (state.speed < 1f) {
            saveStoppedState(stack, tick);
            CogwheelShieldStateManager.remove(player);
            return;
        }

        shield.onServerSpeedChanged(player, stack, oldSpeed, state.speed);
    }

    private static boolean tryConsumeAir(ServerPlayer player, ItemStack stack,
                                         BaseCogwheelShieldItem shield,
                                         CogwheelShieldStateManager.State state,
                                         long tick) {
        if (!shield.canConsumeBacktankAir(stack, player))
            return false;

        var tanks = BacktankUtil.getAllWithAir(player);

        if (tanks.isEmpty())
            return false;

        if (tick - state.lastAirTick < shield.getAirConsumeIntervalTicks(stack))
            return true;

        int pneumaticBoostLevel = shield.getPneumaticBoostLevel(stack, player);
        int airCost = shield.getAirCost(stack, player, pneumaticBoostLevel);
        ItemStack tank = tanks.getFirst();

        if (BacktankUtil.getAir(tank) < airCost)
            return false;

        BacktankUtil.consumeAir(player, tank, airCost);
        state.lastAirTick = tick;

        return true;
    }

    private static void saveStoppedState(ItemStack stack, long tick) {
        stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        stack.set(CSDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), tick);
    }
}