package com.erix.creatorsword.network;

import com.erix.creatorsword.advancement.CreatorSwordCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ShieldFullSpeedPayload.TYPE,
                ShieldFullSpeedPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    CreatorSwordCriteriaTriggers.FULL_SPEED.get().trigger(player);
                }
        );
    }
}
