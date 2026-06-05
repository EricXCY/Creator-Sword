package com.erix.creatorsword.item.cogwheel_shield;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class CogwheelShieldClientSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.register(CogwheelShieldClientSetup.class);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                CogwheelShieldItems.COGWHEEL_SHIELD_ENTITY.get(),
                BaseCogwheelShieldEntityRenderer::new
        );
    }
}