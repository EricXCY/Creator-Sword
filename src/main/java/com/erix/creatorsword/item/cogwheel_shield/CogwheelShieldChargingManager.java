package com.erix.creatorsword.item.cogwheel_shield;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CogwheelShieldChargingManager {
    private static final Set<UUID> ACTIVE_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> CHARGING_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> DECAYING_PLAYERS = ConcurrentHashMap.newKeySet();

    private static final Map<UUID, Float> SERVER_SPEEDS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_AIR_TICKS = new ConcurrentHashMap<>();

    public static void start(ServerPlayer player, float initialSpeed) {
        UUID id = player.getUUID();

        ACTIVE_PLAYERS.add(id);
        CHARGING_PLAYERS.add(id);
        DECAYING_PLAYERS.remove(id);

        SERVER_SPEEDS.put(id, initialSpeed);
    }

    public static void stop(ServerPlayer player) {
        UUID id = player.getUUID();

        CHARGING_PLAYERS.remove(id);

        float speed = SERVER_SPEEDS.getOrDefault(id, 0f);

        if (speed > 0f) {
            ACTIVE_PLAYERS.add(id);
            DECAYING_PLAYERS.add(id);
        } else {
            remove(player);
        }
    }

    public static void remove(ServerPlayer player) {
        UUID id = player.getUUID();

        ACTIVE_PLAYERS.remove(id);
        CHARGING_PLAYERS.remove(id);
        DECAYING_PLAYERS.remove(id);

        SERVER_SPEEDS.remove(id);
        LAST_AIR_TICKS.remove(id);
    }

    public static boolean isActive(ServerPlayer player) {
        return ACTIVE_PLAYERS.contains(player.getUUID());
    }

    public static boolean isCharging(ServerPlayer player) {
        return CHARGING_PLAYERS.contains(player.getUUID());
    }

    public static boolean isDecaying(ServerPlayer player) {
        return DECAYING_PLAYERS.contains(player.getUUID());
    }

    public static float getSpeed(ServerPlayer player) {
        return SERVER_SPEEDS.getOrDefault(player.getUUID(), 0f);
    }

    public static void setSpeed(ServerPlayer player, float speed) {
        SERVER_SPEEDS.put(player.getUUID(), speed);
    }

    public static long getLastAirTick(ServerPlayer player) {
        return LAST_AIR_TICKS.getOrDefault(player.getUUID(), 0L);
    }

    public static void setLastAirTick(ServerPlayer player, long tick) {
        LAST_AIR_TICKS.put(player.getUUID(), tick);
    }
}