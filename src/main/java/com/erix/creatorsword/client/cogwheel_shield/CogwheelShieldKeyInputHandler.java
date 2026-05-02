package com.erix.creatorsword.client.cogwheel_shield;

import com.erix.creatorsword.client.KeyBindings;
import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldFullSpeedPayload;
import com.erix.creatorsword.network.ShieldThrowPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class CogwheelShieldKeyInputHandler {
    private static final float THROW_SPEED_THRESHOLD = 64f;
    private static final float FULL_SPEED = 256f;

    private static boolean wasDown = false;
    private static boolean sentFullSpeedThisHold = false;

    private static long lastRenderTimeNanos = 0L;

    private static float offhandAngle = 0f;
    private static float mainhandAngle = 0f;

    private static float offhandSpeed = 0f;
    private static float mainhandSpeed = 0f;

    private static boolean offhandCharging = false;
    private static boolean mainhandCharging = false;

    private static boolean offhandDecaying = false;
    private static boolean mainhandDecaying = false;

    private static long offhandLastUpdateTick = 0L;
    private static long mainhandLastUpdateTick = 0L;

    private static ItemStack lastOffhandStack = ItemStack.EMPTY;
    private static ItemStack lastMainhandStack = ItemStack.EMPTY;

    private static boolean lastOffhandWasShield = false;
    private static boolean lastMainhandWasShield = false;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen != null)
            return;

        LocalPlayer player = mc.player;

        if (player == null)
            return;

        boolean isDown = KeyBindings.ROTATE_COGWHEEL.isDown();

        ItemStack offhand = player.getOffhandItem();
        ItemStack mainhand = player.getMainHandItem();

        syncOrLoadStateWhenHandLayoutChanged(offhand, mainhand);

        tickShieldLogic(offhand, true, isDown, player);
        tickShieldLogic(mainhand, false, isDown, player);

        handleFullSpeedPacket(offhand, mainhand, isDown);
        handleThrowOnKeyRelease(offhand, isDown);

        wasDown = isDown;
    }

    public static void clientRenderFrame() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            lastRenderTimeNanos = 0L;
            return;
        }

        long now = System.nanoTime();

        if (lastRenderTimeNanos == 0L) {
            lastRenderTimeNanos = now;
            return;
        }

        float deltaSeconds = (now - lastRenderTimeNanos) / 1_000_000_000.0f;
        lastRenderTimeNanos = now;

        deltaSeconds = Math.min(deltaSeconds, 0.05f);

        updateVisualRotation(true, deltaSeconds);
        updateVisualRotation(false, deltaSeconds);
    }

    private static void syncOrLoadStateWhenHandLayoutChanged(ItemStack offhand, ItemStack mainhand) {
        boolean offhandIsShield = offhand.getItem() instanceof CogwheelShieldItem;
        boolean mainhandIsShield = mainhand.getItem() instanceof CogwheelShieldItem;

        boolean offhandChanged = offhand != lastOffhandStack;
        boolean mainhandChanged = mainhand != lastMainhandStack;
        boolean bothHandsChanged = offhandChanged && mainhandChanged;

        if (bothHandsChanged
                && lastOffhandWasShield
                && lastMainhandWasShield
                && offhandIsShield
                && mainhandIsShield) {

            swapHandStates();
            updateLastHandState(offhand, mainhand, offhandIsShield, mainhandIsShield);
            return;
        }

        if (bothHandsChanged && movedOffhandShieldToMainhand(offhandIsShield, mainhandIsShield)) {
            copyOffhandStateToMainhand();
            updateLastHandState(offhand, mainhand, offhandIsShield, mainhandIsShield);
            return;
        }

        if (bothHandsChanged && movedMainhandShieldToOffhand(offhandIsShield, mainhandIsShield)) {
            copyMainhandStateToOffhand();
            updateLastHandState(offhand, mainhand, offhandIsShield, mainhandIsShield);
            return;
        }

        if (offhandChanged) {
            if (offhandIsShield && !lastOffhandWasShield) {
                loadOffhandStateFromStack(offhand);
            } else if (!offhandIsShield && lastOffhandWasShield) {
                clearOffhandState();
            }
        }

        if (mainhandChanged) {
            if (mainhandIsShield && !lastMainhandWasShield) {
                loadMainhandStateFromStack(mainhand);
            } else if (!mainhandIsShield && lastMainhandWasShield) {
                clearMainhandState();
            }
        }

        updateLastHandState(offhand, mainhand, offhandIsShield, mainhandIsShield);
    }

    private static boolean movedOffhandShieldToMainhand(boolean offhandIsShield, boolean mainhandIsShield) {
        return lastOffhandWasShield
                && !lastMainhandWasShield
                && !offhandIsShield
                && mainhandIsShield;
    }

    private static boolean movedMainhandShieldToOffhand(boolean offhandIsShield, boolean mainhandIsShield) {
        return !lastOffhandWasShield
                && lastMainhandWasShield
                && offhandIsShield
                && !mainhandIsShield;
    }

    private static void updateLastHandState(ItemStack offhand, ItemStack mainhand,
                                            boolean offhandIsShield, boolean mainhandIsShield) {
        lastOffhandStack = offhand;
        lastMainhandStack = mainhand;
        lastOffhandWasShield = offhandIsShield;
        lastMainhandWasShield = mainhandIsShield;
    }

    private static void loadOffhandStateFromStack(ItemStack stack) {
        offhandSpeed = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        offhandCharging = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        offhandDecaying = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_DECAYING.get(), false) || offhandSpeed > 0f;
        offhandLastUpdateTick = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
    }

    private static void loadMainhandStateFromStack(ItemStack stack) {
        mainhandSpeed = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        mainhandCharging = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        mainhandDecaying = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_DECAYING.get(), false) || mainhandSpeed > 0f;
        mainhandLastUpdateTick = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), 0L);
    }

    private static void copyOffhandStateToMainhand() {
        mainhandSpeed = offhandSpeed;
        mainhandCharging = offhandCharging;
        mainhandDecaying = offhandDecaying;
        mainhandLastUpdateTick = offhandLastUpdateTick;

        clearOffhandState();
    }

    private static void copyMainhandStateToOffhand() {
        offhandSpeed = mainhandSpeed;
        offhandCharging = mainhandCharging;
        offhandDecaying = mainhandDecaying;
        offhandLastUpdateTick = mainhandLastUpdateTick;

        clearMainhandState();
    }

    private static void swapHandStates() {
        float speed = offhandSpeed;
        boolean charging = offhandCharging;
        boolean decaying = offhandDecaying;
        long lastUpdateTick = offhandLastUpdateTick;
        float angle = offhandAngle;

        offhandSpeed = mainhandSpeed;
        offhandCharging = mainhandCharging;
        offhandDecaying = mainhandDecaying;
        offhandLastUpdateTick = mainhandLastUpdateTick;
        offhandAngle = mainhandAngle;

        mainhandSpeed = speed;
        mainhandCharging = charging;
        mainhandDecaying = decaying;
        mainhandLastUpdateTick = lastUpdateTick;
        mainhandAngle = angle;
    }

    private static void clearOffhandState() {
        offhandSpeed = 0f;
        offhandCharging = false;
        offhandDecaying = false;
        offhandLastUpdateTick = 0L;
    }

    private static void clearMainhandState() {
        mainhandSpeed = 0f;
        mainhandCharging = false;
        mainhandDecaying = false;
        mainhandLastUpdateTick = 0L;
    }

    private static void tickShieldLogic(ItemStack stack, boolean offhand, boolean isKeyDown, LocalPlayer player) {
        if (!(stack.getItem() instanceof CogwheelShieldItem shield))
            return;

        long currentTick = player.level().getGameTime();
        long lastUpdateTick = offhand ? offhandLastUpdateTick : mainhandLastUpdateTick;

        if (currentTick - lastUpdateTick < CogwheelShieldItem.UPDATE_INTERVAL_TICKS)
            return;

        if (offhand) {
            offhandLastUpdateTick = currentTick;
        } else {
            mainhandLastUpdateTick = currentTick;
        }

        float speed = getSpeed(offhand);
        boolean charging = isCharging(offhand);
        boolean decaying = isDecaying(offhand);

        if (isKeyDown) {
            if (!charging) {
                charging = true;
                decaying = false;
            }

            float accelFactor = shield.getClientAccelerationFactor(stack, player, currentTick);
            float nextSpeed = speed * accelFactor;

            speed = Math.min(
                    Math.max(nextSpeed, CogwheelShieldItem.MIN_SPEED),
                    shield.getMaxSpeed(stack, player)
            );

        } else {
            if (charging) {
                charging = false;
                decaying = true;
            }

            if (decaying) {
                speed *= CogwheelShieldItem.DECAY_RATE;

                if (speed < 1f) {
                    speed = 0f;
                    decaying = false;
                }
            }
        }

        setClientState(offhand, speed, charging, decaying);
    }

    private static void updateVisualRotation(boolean offhand, float deltaSeconds) {
        float speed = getSpeed(offhand);

        if (speed <= 0.01f)
            return;

        float degreesPerSecond = speed * 6f;
        float deltaAngle = degreesPerSecond * deltaSeconds;

        if (offhand) {
            offhandAngle = normalizeAngle(offhandAngle + deltaAngle);
        } else {
            mainhandAngle = normalizeAngle(mainhandAngle - deltaAngle);
        }
    }

    private static float normalizeAngle(float angle) {
        angle %= 360f;

        if (angle < 0f)
            angle += 360f;

        return angle;
    }

    public static float getOffhandAngle() {
        return offhandAngle;
    }

    public static float getMainhandAngle() {
        return mainhandAngle;
    }

    public static float getSpeed(boolean offhand) {
        return offhand ? offhandSpeed : mainhandSpeed;
    }

    public static boolean isCharging(boolean offhand) {
        return offhand ? offhandCharging : mainhandCharging;
    }

    public static boolean isDecaying(boolean offhand) {
        return offhand ? offhandDecaying : mainhandDecaying;
    }

    private static void setClientState(boolean offhand, float speed, boolean charging, boolean decaying) {
        if (offhand) {
            offhandSpeed = speed;
            offhandCharging = charging;
            offhandDecaying = decaying;
        } else {
            mainhandSpeed = speed;
            mainhandCharging = charging;
            mainhandDecaying = decaying;
        }
    }

    private static void handleFullSpeedPacket(ItemStack offhand, ItemStack mainhand, boolean isDown) {
        if (isDown && !sentFullSpeedThisHold) {
            if (isShieldAtFullSpeed(offhand, true) && isShieldAtFullSpeed(mainhand, false)) {
                PacketDistributor.sendToServer(new ShieldFullSpeedPayload(offhandSpeed, mainhandSpeed));
                sentFullSpeedThisHold = true;
            }
        }

        if (!isDown) {
            sentFullSpeedThisHold = false;
        }
    }

    private static void handleThrowOnKeyRelease(ItemStack offhand, boolean isDown) {
        if (isDown || !wasDown)
            return;

        if (!(offhand.getItem() instanceof CogwheelShieldItem))
            return;

        float speed = offhandSpeed;

        if (speed >= THROW_SPEED_THRESHOLD) {
            triggerThrowShield(offhand, speed);
        }
    }

    public static void triggerThrowShield(ItemStack stack, float speed) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null)
            return;

        stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), speed);
        stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        stack.set(CSDataComponents.GEAR_SHIELD_LAST_UPDATE.get(), mc.player.level().getGameTime());

        PacketDistributor.sendToServer(new ShieldThrowPayload(speed));
    }

    private static boolean isShieldAtFullSpeed(ItemStack stack, boolean offhand) {
        if (!(stack.getItem() instanceof CogwheelShieldItem))
            return false;

        return getSpeed(offhand) >= FULL_SPEED;
    }
}