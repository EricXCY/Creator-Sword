package com.erix.creatorsword.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ShieldStatePayload(
        float speed,
        boolean charging,
        boolean decaying,
        long chargeStart,
        long lastDecay,
        boolean isOffhand
) implements CustomPacketPayload {
    public static final Type<ShieldStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("creatorsword", "shield_state"));

    public static final StreamCodec<ByteBuf, ShieldStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, ShieldStatePayload::speed,
                    ByteBufCodecs.BOOL, ShieldStatePayload::charging,
                    ByteBufCodecs.BOOL, ShieldStatePayload::decaying,
                    ByteBufCodecs.VAR_LONG, ShieldStatePayload::chargeStart,
                    ByteBufCodecs.VAR_LONG, ShieldStatePayload::lastDecay,
                    ByteBufCodecs.BOOL, ShieldStatePayload::isOffhand,
                    ShieldStatePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
