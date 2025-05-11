package com.erix.creatorsword.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import com.erix.creatorsword.network.ShieldSpeedPayload;

public class ModNetwork {
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // 使用协议版本 "1"
        event.registrar("1")
                .playBidirectional(
                        ShieldSpeedPayload.TYPE,
                        ShieldSpeedPayload.STREAM_CODEC,
                        (payload, ctx) -> {
                            // 服务端接收后写入静态字段
                            com.erix.creatorsword.client.ClientTickHandler.currentSpeed
                                    = payload.speed();
                        }
                );
    }
}
