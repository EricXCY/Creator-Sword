package com.erix.creatorsword.network;

import com.erix.creatorsword.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.entity.ThrownCogwheelShield;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ShieldFullSpeedPayload.TYPE,
                ShieldFullSpeedPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player != null) {
                        CreatorSwordCriteriaTriggers.FULL_SPEED.get().trigger(player);
                    }
                }
        );

        registrar.playToServer(
                ShieldThrowPayload.TYPE,
                ShieldThrowPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player != null) {
                        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
                        if (stack.getItem() instanceof CogwheelShieldItem && stack.is(payload.stack().getItem())) {
                            ThrownCogwheelShield projectile = new ThrownCogwheelShield(player.level(), player, payload.speed(), stack.copy());
                            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                            player.level().addFreshEntity(projectile);
                            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                        }
                    }
                }
        );

        registrar.playToServer(
                ShieldStatePayload.TYPE,
                ShieldStatePayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player == null) return;

                    InteractionHand hand = payload.isOffhand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    ItemStack serverStack = player.getItemInHand(hand);

                    if (!(serverStack.getItem() instanceof CogwheelShieldItem)) return;

                    ItemStack clientStack = payload.stack();

                    float clientSpeed = clientStack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
                    boolean clientCharging = clientStack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false);
                    boolean clientDecaying = clientStack.getOrDefault(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false);
                    long clientChargeStart = clientStack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L);
                    long clientLastDecay = clientStack.getOrDefault(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L);
                    float clientAngle = clientStack.getOrDefault(ModDataComponents.GEAR_SHIELD_ANGLE.get(), 0f);

                    if (Math.abs(serverStack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f) - clientSpeed) > 0.01f)
                        serverStack.set(ModDataComponents.GEAR_SHIELD_SPEED.get(), clientSpeed);

                    if (serverStack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGING.get(), false) != clientCharging)
                        serverStack.set(ModDataComponents.GEAR_SHIELD_CHARGING.get(), clientCharging);

                    if (serverStack.getOrDefault(ModDataComponents.GEAR_SHIELD_DECAYING.get(), false) != clientDecaying)
                        serverStack.set(ModDataComponents.GEAR_SHIELD_DECAYING.get(), clientDecaying);

                    if (serverStack.getOrDefault(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), 0L) != clientChargeStart)
                        serverStack.set(ModDataComponents.GEAR_SHIELD_CHARGE_START.get(), clientChargeStart);

                    if (serverStack.getOrDefault(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), 0L) != clientLastDecay)
                        serverStack.set(ModDataComponents.GEAR_SHIELD_LAST_DECAY.get(), clientLastDecay);

                    if (Math.abs(serverStack.getOrDefault(ModDataComponents.GEAR_SHIELD_ANGLE.get(), 0f) - clientAngle) > 0.01f)
                        serverStack.set(ModDataComponents.GEAR_SHIELD_ANGLE.get(), clientAngle);
                }
        );

    }
}