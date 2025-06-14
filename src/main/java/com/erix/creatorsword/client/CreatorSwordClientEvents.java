package com.erix.creatorsword.client;

import com.erix.creatorsword.KeyBindings;
import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldStatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class CreatorSwordClientEvents {
    private static final float THROW_SPEED_THRESHOLD = 64f;
    private static boolean wasVPressed = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        KeyInputHandler.clientTick();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean isVPressed = KeyBindings.ROTATE_COGWHEEL.isDown();
        if (!isVPressed && wasVPressed) {
            ItemStack stack = mc.player.getItemInHand(InteractionHand.OFF_HAND);
            if (stack.getItem() instanceof CogwheelShieldItem) {
                float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
                if (speed >= THROW_SPEED_THRESHOLD) {
                    KeyInputHandler.triggerThrowShield(stack, speed, true);
                }
            }
        }
        wasVPressed = isVPressed;
    }

    private static void syncShieldState(ItemStack stack, boolean isOffhand) {
        if (!(stack.getItem() instanceof CogwheelShieldItem)) return;

        float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        boolean charging = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        long chargeStart = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);

        if (speed > 0 && !charging) {
            stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
            stack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), System.currentTimeMillis());
            PacketDistributor.sendToServer(new ShieldStatePayload(speed, charging, true, chargeStart, System.currentTimeMillis(), isOffhand));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        syncShieldState(mc.player.getOffhandItem(), true);
        syncShieldState(mc.player.getMainHandItem(), false);
    }


    private static void resetAndSyncShield(ItemStack stack, boolean isOffhand) {
        if (!(stack.getItem() instanceof CogwheelShieldItem)) return;

        KeyInputHandler.resetNBT(stack);
        PacketDistributor.sendToServer(new ShieldStatePayload(
                0f, false, false, 0L, 0L, isOffhand
        ));
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        resetAndSyncShield(mc.player.getOffhandItem(), true);
        resetAndSyncShield(mc.player.getMainHandItem(), false);
    }

}