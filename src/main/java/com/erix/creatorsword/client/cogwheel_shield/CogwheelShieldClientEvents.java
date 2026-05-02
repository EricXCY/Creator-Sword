package com.erix.creatorsword.client.cogwheel_shield;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import com.erix.creatorsword.network.ShieldStatePayload;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public class CogwheelShieldClientEvents {

    public static void register() {
        NeoForge.EVENT_BUS.register(CogwheelShieldClientEvents.class);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen != null)
            return;

        CogwheelShieldKeyInputHandler.clientTick();
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderGuiEvent.Post event) {
        CogwheelShieldKeyInputHandler.clientRenderFrame();
    }

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null)
            return;

        syncShieldState(mc.player.getOffhandItem(), true);
        syncShieldState(mc.player.getMainHandItem(), false);
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        resetAndSyncShield(mc.player.getOffhandItem(), true);
        resetAndSyncShield(mc.player.getMainHandItem(), false);
    }

    @SubscribeEvent
    public static void onRenderHUD(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null)
            return;

        ItemStack offhand = player.getOffhandItem();

        if (!(offhand.getItem() instanceof CogwheelShieldItem))
            return;

        if (!player.isUsingItem() || player.getUseItem() != offhand)
            return;

        float speed = CogwheelShieldKeyInputHandler.getSpeed(true);

        if (speed < 0.01f)
            return;

        renderSpeedHud(event.getGuiGraphics(), speed);
    }

    private static void syncShieldState(ItemStack stack, boolean isOffhand) {
        if (!(stack.getItem() instanceof CogwheelShieldItem))
            return;

        float speed = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        boolean charging = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
        boolean decaying = stack.getOrDefault(CSDataComponents.GEAR_SHIELD_DECAYING.get(), false);

        if (speed > 0) {
            PacketDistributor.sendToServer(new ShieldStatePayload(isOffhand, speed, charging, decaying));
        }
    }

    private static void resetAndSyncShield(ItemStack stack, boolean isOffhand) {
        if (!(stack.getItem() instanceof CogwheelShieldItem))
            return;

        CogwheelShieldItem.resetNBT(stack);

        PacketDistributor.sendToServer(new ShieldStatePayload(
                isOffhand,
                0f,
                false,
                true
        ));
    }

    private static void renderSpeedHud(GuiGraphics guiGraphics, float speed) {
        Minecraft mc = Minecraft.getInstance();

        String text = String.format("%.1f RPM", speed);

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = screenWidth - mc.font.width(text) - 7;
        int y = screenHeight - 10;

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        if (speed >= 512f) {
            renderRainbowText(guiGraphics, text, x, y);
        } else {
            guiGraphics.drawString(mc.font, text, x, y, getColorFromSpeed(speed), true);
        }

        poseStack.popPose();
    }

    private static void renderRainbowText(GuiGraphics guiGraphics, String text, int x, int y) {
        Minecraft mc = Minecraft.getInstance();

        long time = System.currentTimeMillis();
        int charX = x;

        for (int i = 0; i < text.length(); i++) {
            float hue = (float) ((i * 0.13f + (time % 2000L) / 2000.0) % 1.0f);
            int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);

            String c = String.valueOf(text.charAt(i));
            guiGraphics.drawString(mc.font, c, charX, y, rgb, true);
            charX += mc.font.width(c);
        }
    }

    private static int getColorFromSpeed(float speed) {
        if (speed < 64f)
            return 0xFFFFFF;
        if (speed < 128f)
            return 0x22FF22;
        if (speed < 256f)
            return 0x0084FF;
        if (speed < 512f)
            return 0xFF55FF;

        return 0xFFFFFF;
    }
}