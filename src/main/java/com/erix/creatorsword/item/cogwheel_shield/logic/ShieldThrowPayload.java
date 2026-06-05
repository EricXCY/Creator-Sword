package com.erix.creatorsword.item.cogwheel_shield.logic;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShieldThrowPayload(float speed) implements CustomPacketPayload {
    public static final Type<ShieldThrowPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "shield_throw"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldThrowPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeFloat(payload.speed()),
                    buf -> new ShieldThrowPayload(buf.readFloat())
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}