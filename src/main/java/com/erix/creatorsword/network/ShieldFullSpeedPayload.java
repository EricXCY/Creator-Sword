package com.erix.creatorsword.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ShieldFullSpeedPayload() implements CustomPacketPayload {
    public static final Type<ShieldFullSpeedPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("creatorsword", "shield_full_speed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldFullSpeedPayload> STREAM_CODEC =
            StreamCodec.unit(new ShieldFullSpeedPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
