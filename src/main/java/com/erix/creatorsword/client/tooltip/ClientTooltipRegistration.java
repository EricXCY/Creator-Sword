package com.erix.creatorsword.client.tooltip;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT)
public class ClientTooltipRegistration {

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CaptureBoxTooltip.class,
                t -> new CaptureBoxTooltipClient(t.getEntityTypeId(), t.getEntityNbt()));
    }
}
