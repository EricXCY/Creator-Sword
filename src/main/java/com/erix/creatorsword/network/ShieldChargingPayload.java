package com.erix.creatorsword.network;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShieldChargingPayload(boolean charging) implements CustomPacketPayload {
    public static final Type<ShieldChargingPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "shield_charging"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldChargingPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ShieldChargingPayload::charging,
                    ShieldChargingPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}