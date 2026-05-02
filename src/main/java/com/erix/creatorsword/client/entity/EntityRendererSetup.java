package com.erix.creatorsword.client.entity;

import com.erix.creatorsword.entity.CSEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.register(EntityRendererSetup.class);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                CSEntities.COGWHEEL_SHIELD_ENTITY.get(),
                ThrownCogwheelShieldRenderer::new
        );
    }
}