package com.erix.creatorsword.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ShieldStatePayload(ItemStack stack, boolean isOffhand) implements CustomPacketPayload {
    public static final Type<ShieldStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("creatorsword", "shield_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ShieldStatePayload::stack,
                    ByteBufCodecs.BOOL, ShieldStatePayload::isOffhand,
                    ShieldStatePayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}