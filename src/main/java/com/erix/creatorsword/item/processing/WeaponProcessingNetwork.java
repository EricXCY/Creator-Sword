package com.erix.creatorsword.item.processing;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = CreatorSword.MODID)
public final class WeaponProcessingNetwork {
    private WeaponProcessingNetwork() {}

    public record Process(boolean attack) implements CustomPacketPayload {
        public static final Type<Process> TYPE = new Type<>(CreatorSword.asResource("weapon_process"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Process> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Process::attack, Process::new);
        @Override public Type<Process> type() { return TYPE; }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Process.TYPE, Process.CODEC, (payload, context) ->
                context.enqueueWork(() -> {
                    if (!(context.player() instanceof ServerPlayer player)) return;
                    var kind = ProcessingKind.of(player.getMainHandItem());
                    if (kind != null && kind.usesAttack() == payload.attack())
                        WeaponRecipeProcessing.process(player);
                }));
    }
}
