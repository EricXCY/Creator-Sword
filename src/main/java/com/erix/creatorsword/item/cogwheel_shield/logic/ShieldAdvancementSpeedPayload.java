package com.erix.creatorsword.item.cogwheel_shield.logic;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShieldAdvancementSpeedPayload(float offhandSpeed, float mainhandSpeed) implements CustomPacketPayload {
    public static final Type<ShieldAdvancementSpeedPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "shield_advancement_speed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldAdvancementSpeedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, ShieldAdvancementSpeedPayload::offhandSpeed,
                    ByteBufCodecs.FLOAT, ShieldAdvancementSpeedPayload::mainhandSpeed,
                    ShieldAdvancementSpeedPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}