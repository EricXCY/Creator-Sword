package com.erix.creatorsword.client;

import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldStatePayload;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class CreatorSwordClientEvents {
    private static final float THROW_SPEED_THRESHOLD = 64f;
    private static boolean wasVPressed = false;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
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
                    KeyInputHandler.triggerThrowShield(stack, speed);
                }
            }
        }
        wasVPressed = isVPressed;
    }

    private static void syncShieldState(ItemStack stack, boolean isOffhand) {
        if (!(stack.getItem() instanceof CogwheelShieldItem)) return;

        float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        boolean charging = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);

        if (speed > 0 && !charging) {
            stack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), true);
            PacketDistributor.sendToServer(new ShieldStatePayload(stack, isOffhand));
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

        CogwheelShieldItem.resetNBT(stack);
        PacketDistributor.sendToServer(new ShieldStatePayload(
                stack, isOffhand
        ));
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        resetAndSyncShield(mc.player.getOffhandItem(), true);
        resetAndSyncShield(mc.player.getMainHandItem(), false);
    }

    @SubscribeEvent
    public static void onRenderHUD(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // 检查是否正在使用副手的齿轮盾
        ItemStack offhand = player.getOffhandItem();
        if (!(offhand.getItem() instanceof CogwheelShieldItem)) return;
        if (!player.isUsingItem() || player.getUseItem() != offhand) return;

        // 读取转速
        float speed = offhand.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        if (speed < 0.01f) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        String text = String.format("%.1f RPM", speed);

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = screenWidth - mc.font.width(text) - 7;
        int y = screenHeight - 10;

        int color = getColorFromSpeed(speed);
        poseStack.pushPose();
        if (speed >= 512f) {
            long time = System.currentTimeMillis();
            int charX = x;
            for (int i = 0; i < text.length(); i++) {
                float hue = (float) ((i * 0.13f + (time % 2000L) / 2000.0) % 1.0f);
                int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                String c = String.valueOf(text.charAt(i));
                guiGraphics.drawString(mc.font, c, charX, y, rgb, true); // 带阴影
                charX += mc.font.width(c);
            }
        } else {
            guiGraphics.drawString(mc.font, text, x, y, color, true);
        }

        poseStack.popPose();
    }

    private static int getColorFromSpeed(float speed) {
        if (speed < 64f) return 0xFFFFFF;
        else if (speed < 128f) return 0x22FF22;
        else if (speed < 256f) return 0x0084FF;
        else if (speed < 512f) return 0xFF55FF;
        else return 0xFFFFFF;
    }
}