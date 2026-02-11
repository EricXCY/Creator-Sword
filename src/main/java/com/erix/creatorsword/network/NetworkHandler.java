package com.erix.creatorsword.network;

import com.erix.creatorsword.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.data.ShieldDataComponents;
import com.erix.creatorsword.entity.ThrownCogwheelShield;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    private static final String THROWN_SHIELD_TAG = "creatorsword_thrown_shield";

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
                    if (player == null) return;

                    ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (!(off.getItem() instanceof CogwheelShieldItem)) return;

                    CompoundTag pd = player.getPersistentData();
                    if (pd.contains(THROWN_SHIELD_TAG)) return;

                    // 防止附近已经有自己的投掷盾
                    if (!player.level().getEntitiesOfClass(
                            ThrownCogwheelShield.class,
                            player.getBoundingBox().inflate(5),
                            e -> e.getOwner() == player
                    ).isEmpty()) return;

                    pd.put(THROWN_SHIELD_TAG, off.save(player.registryAccess()));

                    ThrownCogwheelShield projectile =
                            new ThrownCogwheelShield(player.level(), player, payload.speed(), off.copy());
                    projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                    player.level().addFreshEntity(projectile);

                    player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
        );

        registrar.playToServer(
                ShieldStatePayload.TYPE,
                ShieldStatePayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player == null) return;

                    InteractionHand hand = payload.offhand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    ItemStack serverStack = player.getItemInHand(hand);

                    if (!(serverStack.getItem() instanceof CogwheelShieldItem)) return;

                    ItemStack clientStack = payload.stack();

                    float clientSpeed = clientStack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
                    boolean clientCharging = clientStack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_CHARGING.get(), false);
                    boolean clientDecaying = clientStack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_DECAYING.get(), true);

                    if (Math.abs(serverStack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_SPEED.get(), 0f) - clientSpeed) > 0.01f)
                        serverStack.set(ShieldDataComponents.GEAR_SHIELD_SPEED.get(), clientSpeed);

                    if (serverStack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_CHARGING.get(), false) != clientCharging)
                        serverStack.set(ShieldDataComponents.GEAR_SHIELD_CHARGING.get(), clientCharging);

                    if (serverStack.getOrDefault(ShieldDataComponents.GEAR_SHIELD_DECAYING.get(), false) != clientDecaying)
                        serverStack.set(ShieldDataComponents.GEAR_SHIELD_DECAYING.get(), clientDecaying);
                }
        );

    }
}