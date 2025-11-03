package com.erix.creatorsword.client;

import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldThrowPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class KeyInputHandler {
    private static boolean wasDown = false;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (Minecraft.getInstance().screen != null)
            return;

        if (mc.player == null) return;

        boolean isDown = KeyBindings.ROTATE_COGWHEEL.isDown();
        ItemStack off = mc.player.getOffhandItem();
        ItemStack main = mc.player.getMainHandItem();

        if (off.getItem() instanceof CogwheelShieldItem shieldOff)
            shieldOff.tickClient(off, mc.player, isDown);

        if (main.getItem() instanceof CogwheelShieldItem shieldMain)
            shieldMain.tickClient(main, mc.player, isDown);

        updateRotation(off, true);
        updateRotation(main, false);

        if (!isDown && wasDown) {

            ItemStack stack = ItemStack.EMPTY;
            if (off.getItem() instanceof CogwheelShieldItem) stack = off;

            if (!stack.isEmpty()) {
                float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
                if (speed >= 64f) {
                    triggerThrowShield(stack, speed);
                    if (off == stack) mc.player.getInventory().offhand.set(0, ItemStack.EMPTY);
                }
            }
        }

        wasDown = isDown;
    }

    private static void updateRotation(ItemStack stack, boolean offhand) {
        float angle = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_ANGLE.get(), 0f);
        float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        angle += (offhand ? 1 : -1) * (speed / 20f);
        angle = (angle + 360f) % 360f;
        stack.set(ModDataComponents.GEAR_SHIELD_ANGLE.get(), angle);
    }

    public static void triggerThrowShield(ItemStack stack, float speed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
        PacketDistributor.sendToServer(new ShieldThrowPayload(speed, stack));
    }
}
