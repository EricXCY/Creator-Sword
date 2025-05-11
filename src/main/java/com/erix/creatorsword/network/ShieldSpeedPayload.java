package com.erix.creatorsword.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

/**
 * 客户端→服务器：同步齿轮盾牌当前转速。
 */
public record ShieldSpeedPayload(float speed) implements CustomPacketPayload {
    /** 全局唯一的包类型标识 */
    public static final Type<ShieldSpeedPayload> TYPE =
            CustomPacketPayload.createType(
                    ResourceLocation.fromNamespaceAndPath("creatorsword", "shield_speed")
            );

    /** 编解码器：从 PacketByteBuf 读/写一个 float */
    public static final StreamCodec<PacketByteBuf, ShieldSpeedPayload> STREAM_CODEC =
            CustomPacketPayload.codec(
                    buf -> new ShieldSpeedPayload(buf.readFloat()),    // 读取 speed
                    (payload, buf) -> buf.writeFloat(payload.speed())  // 写入 speed
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(speed);
    }
}
