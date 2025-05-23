package com.erix.creatorsword.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class CreatorSwordClientEvents {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        KeyInputHandler.clientTick();
    }
}


