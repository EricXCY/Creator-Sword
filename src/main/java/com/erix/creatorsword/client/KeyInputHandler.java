package com.erix.creatorsword.client;

import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldFullSpeedPayload;
import com.erix.creatorsword.network.ShieldThrowPayload;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.client.Minecraft;
import com.erix.creatorsword.KeyBindings;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class KeyInputHandler {
    private static boolean wasDown = false;
    private static final float MAX_SPEED = 256f;
    private static final float MIN_SPEED = 8f;
    private static final long DECAY_INTERVAL_MS = 750;
    private static long lastAirConsumeTime = 0;
    private static final long AIR_CONSUME_INTERVAL = 1000;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack off = mc.player.getOffhandItem();
        ItemStack main = mc.player.getMainHandItem();

        if (off.getItem() instanceof CogwheelShieldItem) {
            handleShield(off, true);
            updateRotationAngle(off, true);
        }
        if (main.getItem() instanceof CogwheelShieldItem) {
            handleShield(main, false);
            updateRotationAngle(main, false);
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
        Minecraft mc = Minecraft.getInstance();
        
        if (!(stack.getItem() instanceof CogwheelShieldItem)) {
            resetNBT(stack);
            return;
        }

        boolean isDown = KeyBindings.ROTATE_COGWHEEL.isDown();
        boolean oldCharging = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        boolean oldDecaying = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        float oldSpeed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        long oldChargeStartMs = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);
        long oldLastDecayMs = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L);

        boolean isCharging = oldCharging;
        boolean isDecaying = oldDecaying;
        float speed = oldSpeed;
        long chargeStartMs = oldChargeStartMs;
        long lastDecayMs = oldLastDecayMs;

        // 初始化衰减状态
        if (speed > 0 && !isCharging && !isDecaying) {
            isDecaying = true;
            lastDecayMs = System.currentTimeMillis();
        }

        // 1. 按下开始加速
        if (isDown && !wasDown) {
            isCharging = true;
            isDecaying = false;
            if (speed > 0) {
                float equivalentSeconds = (float) (Math.log(Math.max(speed / MIN_SPEED, 1)) / Math.log(2)) + 1;
                chargeStartMs = System.currentTimeMillis() - (long) (equivalentSeconds * 1000);
            } else {
                chargeStartMs = System.currentTimeMillis();
            }
        }

        // 2. 加速
        if (isDown && isCharging) {
            long now = System.currentTimeMillis();
            long elapsed = now - chargeStartMs;
            float seconds = elapsed / 1000f;

            // 默认加速倍率
            float accelerationFactor = 1f;

            // 检查是否拥有含气背罐，并尝试消耗空气
            if (mc.player != null) {
                var backtanks = BacktankUtil.getAllWithAir(mc.player);
                if (!backtanks.isEmpty()) {
                    int airCost = 1;
                    float currentAir = BacktankUtil.getAir(backtanks.get(0));
                    if (currentAir >= airCost) {
                        accelerationFactor = 1.25f;
                        if (now - lastAirConsumeTime >= AIR_CONSUME_INTERVAL) {
                            BacktankUtil.consumeAir(mc.player, backtanks.get(0), airCost);
                            lastAirConsumeTime = now;
                        }
                    }
                }
            }

            // 应用加速
            speed = seconds >= 1
                    ? (float) Math.min(MIN_SPEED * Math.pow(2, (seconds - 1) * accelerationFactor), MAX_SPEED)
                    : MIN_SPEED;
        }

        // 3. 松开
        if (!isDown && wasDown) {
            isCharging = false;
            isDecaying = true;
            lastDecayMs = System.currentTimeMillis();
        }

        // 4. 减速
        if (isDecaying && !isCharging && speed > 0f) {
            long now = System.currentTimeMillis();
            if (now - lastDecayMs >= DECAY_INTERVAL_MS) {
                lastDecayMs = now;
                speed /= 2f;
                if (speed < MIN_SPEED) {
                    speed = 0f;
                    isDecaying = false;
                }
            }
        }

        // 如果不再加速也不在衰减，则速度清零
        if (!isCharging && !isDecaying && speed != 0f) {
            speed = 0f;
        }

        if (Math.abs(speed - oldSpeed) > 0.01f) {
            stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), speed);
        }
        if (isCharging != oldCharging) {
            stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), isCharging);
        }
        if (chargeStartMs != oldChargeStartMs) {
            stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), chargeStartMs);
        }
        if (isDecaying != oldDecaying) {
            stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), isDecaying);
        }
        if (lastDecayMs != oldLastDecayMs) {
            stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), lastDecayMs);
        }

        // 最后统一写回NBT，避免状态不一致
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), speed);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), isCharging);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), isDecaying);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), chargeStartMs);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), lastDecayMs);
    }


    private static void updateRotationAngle(ItemStack stack, boolean isOffhand) {
        float angle = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_ANGLE.get(), 0f);
        float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);

        if (isOffhand) {
            angle += speed / 20f;
            if (angle < 0f) angle += 360f;
        } else {
            angle -= speed / 20f;
            if (angle > 360f) angle -= 360f;
        }
        stack.set(ModDataComponents.GEAR_SHIELD_ANGLE.get(), angle);
    }


    public static void triggerThrowShield(ItemStack stack, float speed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        PacketDistributor.sendToServer(new ShieldThrowPayload(speed, stack));
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
    }

    static void resetNBT(ItemStack stack) {
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L);
        stack.set(ModDataComponents.GEAR_SHIELD_ANGLE.get(), 0f);
    }
}
