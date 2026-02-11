package com.erix.creatorsword.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShieldStatePayload(boolean offhand, float speed, boolean charging, boolean decaying) implements CustomPacketPayload {
    public static final Type<ShieldStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("creatorsword", "shield_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ShieldStatePayload::offhand,
                    ByteBufCodecs.FLOAT, ShieldStatePayload::speed,
                    ByteBufCodecs.BOOL, ShieldStatePayload::charging,
                    ByteBufCodecs.BOOL, ShieldStatePayload::decaying,
                    ShieldStatePayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}