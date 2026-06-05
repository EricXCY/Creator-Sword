package com.erix.creatorsword.item.trial_key;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class PrecisionTrialKeyContext {
    private static final ThreadLocal<ItemStack> CURRENT_KEY = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_PLAYER = new ThreadLocal<>();

    private PrecisionTrialKeyContext() {
    }

    public static void set(UUID playerId, ItemStack stack) {
        CURRENT_PLAYER.set(playerId);
        CURRENT_KEY.set(stack.copy());
    }

    public static UUID getPlayerId() {
        return CURRENT_PLAYER.get();
    }

    public static ItemStack getKey() {
        return CURRENT_KEY.get();
    }

    public static void clear() {
        CURRENT_PLAYER.remove();
        CURRENT_KEY.remove();
    }
}