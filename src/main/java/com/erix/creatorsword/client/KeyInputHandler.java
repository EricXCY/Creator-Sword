package com.erix.creatorsword.client;

import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldFullSpeedPayload;
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
            handleShield(off);
            updateRotationAngle(off, true);
        }
        if (main.getItem() instanceof CogwheelShieldItem) {
            handleShield(main);
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

    private static void handleShield(ItemStack stack) {
        if (!(stack.getItem() instanceof CogwheelShieldItem)) {
            resetNBT(stack);
            return;
        }

        boolean isDown = KeyBindings.ROTATE_COGWHEEL.isDown();
        boolean isCharging = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        boolean isDecaying = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        long chargeStartMs = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);
        long lastDecayMs = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L);

        // 1. 按下开始充能
        if (isDown && !wasDown) {
            isCharging = true;
            isDecaying = false;
            chargeStartMs = System.currentTimeMillis();
            stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), true);
            stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), chargeStartMs);
            stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
            stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        }

        // 2. 充能中
        if (isDown && isCharging) {
            long elapsed = System.currentTimeMillis() - chargeStartMs;
            float seconds = elapsed / 1000f;
            float newSpeed = seconds >= 1
                    ? (float) Math.min(MIN_SPEED * Math.pow(2, seconds - 1), MAX_SPEED)
                    : MIN_SPEED;
            stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), newSpeed);
        }

        // 3. 松开
        if (!isDown && wasDown && isCharging) {
            isCharging = false;
            stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);

            if (isCharging) {
                triggerThrowShield(stack, speed);
                stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
                isDecaying = false;
                stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
            } else {
                // 进入衰减
                isDecaying = true;
                lastDecayMs = System.currentTimeMillis();
                stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
                stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), lastDecayMs);
            }
        }

        // 4. 衰减
        if (isDecaying && !isCharging && speed > 0f) {
            long now = System.currentTimeMillis();
            if (now - lastDecayMs >= DECAY_INTERVAL_MS) {
                lastDecayMs = now;
                speed /= 2f;
                if (speed < MIN_SPEED) {
                    speed = 0f;
                    isDecaying = false;
                }
                stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), speed);
                stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), lastDecayMs);
                stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), isDecaying);
            }
        }
        if (!isCharging && !isDecaying && speed != 0f) {
                stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        }
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


    private static void triggerThrowShield(ItemStack stack, float speed) {
        System.out.println("投掷齿轮盾！速度：" + speed);
        // TODO: 实际投掷逻辑
    }

    private static void resetNBT(ItemStack stack) {
        stack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);
        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
        stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L);
    }
}
