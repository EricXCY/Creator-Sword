package com.erix.creatorsword.item.cogwheel_shield.logic;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CogwheelShieldStateManager {
    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    private CogwheelShieldStateManager() {
    }

    public static final class State {
        public float speed;
        public boolean charging;
        public boolean decaying;
        public long lastAirTick;

        private State(float speed) {
            this.speed = speed;
        }
    }

    public static State get(ServerPlayer player) {
        return STATES.get(player.getUUID());
    }

    public static State getOrCreate(ServerPlayer player, float initialSpeed) {
        return STATES.computeIfAbsent(player.getUUID(), id -> new State(initialSpeed));
    }

    public static boolean has(ServerPlayer player) {
        return STATES.containsKey(player.getUUID());
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