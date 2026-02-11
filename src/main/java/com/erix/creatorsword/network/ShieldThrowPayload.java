package com.erix.creatorsword.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShieldThrowPayload(float speed) implements CustomPacketPayload {
    public static final Type<ShieldThrowPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("creatorsword", "shield_throw"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldThrowPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, ShieldThrowPayload::speed,
                    ShieldThrowPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}