package com.erix.creatorsword.client;

import com.erix.creatorsword.entity.ModEntities;
import com.erix.creatorsword.item.cogwheel_shield.ThrownCogwheelShieldRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "creatorsword", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRendererSetUp {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COGWHEEL_SHIELD_ENTITY.get(), ThrownCogwheelShieldRenderer::new);
    }
}
