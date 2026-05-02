package com.erix.creatorsword.network;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShieldFullSpeedPayload(float offhandSpeed, float mainhandSpeed) implements CustomPacketPayload {
    public static final Type<ShieldFullSpeedPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "shield_full_speed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldFullSpeedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    ShieldFullSpeedPayload::offhandSpeed,
                    ByteBufCodecs.FLOAT,
                    ShieldFullSpeedPayload::mainhandSpeed,
                    ShieldFullSpeedPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}