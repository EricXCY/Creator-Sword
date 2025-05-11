package com.erix.creatorsword.client;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;


@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT)
public class ClientTickHandler {

    private static int holdTime = 0;
    public static float currentSpeed = 0f;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        boolean tabDown = KeyBindings.TAB_KEY.isDown();
        if (tabDown) {
            holdTime++;
            if (holdTime % 60 == 0 && currentSpeed < 256f)
                currentSpeed = currentSpeed == 0f ? 8f : currentSpeed * 2f;
        } else {
            holdTime = 0;
            long t = 0;
            if (Minecraft.getInstance().level != null) {
                t = Minecraft.getInstance().level.getGameTime();
            }
            if (t % 20 == 0 && currentSpeed > 0f) {
                currentSpeed = Math.max(0f, currentSpeed / 2f);
            }
        }
    }
}
