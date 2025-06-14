package com.erix.creatorsword.client;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.item.cogwheel_shield.ShieldStateCache;
import com.erix.creatorsword.network.ShieldFullSpeedPayload;
import com.erix.creatorsword.network.ShieldStatePayload;
import com.erix.creatorsword.network.ShieldThrowPayload;
import net.minecraft.client.Minecraft;
import com.erix.creatorsword.KeyBindings;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class KeyInputHandler {
    private static boolean wasDown = false;
    private static final float MAX_SPEED = 256f;
    private static final float MIN_SPEED = 8f;
    private static final long DECAY_INTERVAL_MS = 750;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack off = mc.player.getOffhandItem();
        ItemStack main = mc.player.getMainHandItem();

        if (off.getItem() instanceof CogwheelShieldItem) {
            handleShield(off, true);
        }
        if (main.getItem() instanceof CogwheelShieldItem) {
            handleShield(main, false);
        }

        float mainSpeed = main.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        float offSpeed = off.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        boolean mainFull = main.getItem() instanceof CogwheelShieldItem && mainSpeed >= 256f;
        boolean offFull = off.getItem() instanceof CogwheelShieldItem && offSpeed >= 256f;

        if (mainFull && offFull) {
            PacketDistributor.sendToServer(new ShieldFullSpeedPayload());
        }

        wasDown = KeyBindings.ROTATE_COGWHEEL.isDown();
    }

    private static void handleShield(ItemStack stack, boolean isOffhand) {
        ShieldStateCache cache = getCache(stack);

        boolean isDown = KeyBindings.ROTATE_COGWHEEL.isDown();
        long now = System.currentTimeMillis();

        // 初始化衰减状态
        if (cache.speed > 0 && !cache.isCharging && !cache.isDecaying) {
            cache.isDecaying = true;
            cache.lastDecay = now;
        }

        // 1. 按下开始加速
        if (isDown && !wasDown) {
            cache.isCharging = true;
            cache.isDecaying = false;
            if (cache.speed > 0) {
                float equivalentSeconds = (float) (Math.log(Math.max(cache.speed / MIN_SPEED, 1)) / Math.log(2)) + 1;
                cache.chargeStart = now - (long) (equivalentSeconds * 1000);
            } else {
                cache.chargeStart = now;
            }
        }

        // 2. 加速
        if (isDown && cache.isCharging) {
            long elapsed = now - cache.chargeStart;
            float seconds = elapsed / 1000f;
            cache.speed = seconds >= 1
                    ? (float) Math.min(MIN_SPEED * Math.pow(2, seconds - 1), MAX_SPEED)
                    : MIN_SPEED;
        }

        // 3. 松开
        if (!isDown && wasDown) {
            cache.isCharging = false;
            cache.isDecaying = true;
            cache.lastDecay = now;
        }

        // 4. 减速
        if (cache.isDecaying && !cache.isCharging && cache.speed > 0f) {
            if (now - cache.lastDecay >= DECAY_INTERVAL_MS) {
                cache.lastDecay = now;
                cache.speed /= 2f;
                if (cache.speed < MIN_SPEED) {
                    cache.speed = 0f;
                    cache.isDecaying = false;
                }
            }
        }

        // 如果不再加速也不在衰减，则速度清零
        if (!cache.isCharging && !cache.isDecaying && cache.speed != 0f) {
            cache.speed = 0f;
        }

        // 判断是否需要同步
        boolean shouldSend = (now - cache.lastSync) >= 100;
        boolean speedChanged = Math.abs(stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f) - cache.speed) > 0.01f;
        boolean chargingChanged = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false) != cache.isCharging;
        boolean decayingChanged = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false) != cache.isDecaying;

//        if (shouldSend && (speedChanged || chargingChanged || decayingChanged)) {
//            PacketDistributor.sendToServer(new ShieldStatePayload(
//                    cache.speed,
//                    cache.isCharging,
//                    cache.isDecaying,
//                    cache.chargeStart,
//                    cache.lastDecay,
//                    isOffhand
//            ));
//            cache.lastSync = now;
//        }

        // 最后统一写回组件（只写一次）
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), cache.speed);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), cache.isCharging);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), cache.chargeStart);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), cache.isDecaying);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), cache.lastDecay);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_SYNC.get(), cache.lastSync);
    }

    public static void triggerThrowShield(ItemStack stack, float speed, boolean isOffhand) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!(stack.getItem() instanceof CogwheelShieldItem)) return;

//        PacketDistributor.sendToServer(new ShieldThrowPayload(speed, isOffhand, stack));
    }


    static void resetNBT(ItemStack stack) {
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L);
    }

    private static final Map<Integer, ShieldStateCache> shieldStateCache = new HashMap<>();

    private static ShieldStateCache getCache(ItemStack stack) {
        int key = System.identityHashCode(stack);
        return shieldStateCache.computeIfAbsent(key, k -> new ShieldStateCache());
    }
}
