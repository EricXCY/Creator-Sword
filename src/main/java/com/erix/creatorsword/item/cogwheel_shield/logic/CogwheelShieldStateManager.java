package com.erix.creatorsword.item.cogwheel_shield.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import com.erix.creatorsword.data.CSDataComponents;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CogwheelShieldStateManager {
    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    private CogwheelShieldStateManager() {
    }

    public static final class State {
        private final ItemStack stack;
        public float speed;
        public boolean charging;
        public boolean decaying;
        public long lastAirTick;

        private State(ItemStack stack, float speed) {
            this.stack = stack;
            this.speed = speed;
        }
    }

    public static State get(ServerPlayer player) {
        State state = STATES.get(player.getUUID());
        if (state != null && state.stack != CogwheelShieldUtil.getHeldCogwheelShield(player)) {
            state.stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), state.speed);
            state.stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
            state.stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), state.speed > 0);
            remove(player);
            return null;
        }
        return state;
    }

    public static State getOrCreate(ServerPlayer player, float initialSpeed) {
        State existing = get(player);
        if (existing != null) return existing;
        State state = new State(CogwheelShieldUtil.getHeldCogwheelShield(player), initialSpeed);
        STATES.put(player.getUUID(), state);
        return state;
    }

    public static boolean has(ServerPlayer player) {
        return get(player) != null;
    }

    public static void startCharging(ServerPlayer player, float initialSpeed) {
        State state = getOrCreate(player, initialSpeed);

        state.speed = initialSpeed;
        state.charging = true;
        state.decaying = false;
    }

    public static void stopCharging(ServerPlayer player) {
        State state = get(player);

        if (state == null)
            return;

        state.charging = false;
        state.decaying = state.speed > 0f;

        if (!state.decaying) {
            remove(player);
        }
    }

    public static float getSpeed(ServerPlayer player) {
        State state = get(player);
        return state == null ? 0f : state.speed;
    }

    public static void remove(ServerPlayer player) {
        STATES.remove(player.getUUID());
    }
}