package com.erix.creatorsword.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;

public class ClientSetup {
    public static void init(IEventBus modEventBus) {
        if (Dist.CLIENT.isClient()) {
            modEventBus.register(KeyBindings.class);
        }
    }
}