package com.erix.creatorsword.network;

import com.erix.creatorsword.data.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.entity.ThrownCogwheelShield;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.erix.creatorsword.data.CSDataComponents.*;

public class NetworkHandler {
    private static final String THROWN_SHIELD_TAG = "creatorsword_thrown_shield";

    private static final float THROW_SPEED_THRESHOLD = 64f;
    private static final float FULL_SPEED_THRESHOLD = 256f;
    private static final float MAX_SYNC_SPEED = 512f;

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ShieldFullSpeedPayload.TYPE,
                ShieldFullSpeedPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                    ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);

                    if (!(offhand.getItem() instanceof CogwheelShieldItem))
                        return;

                    if (!(mainhand.getItem() instanceof CogwheelShieldItem))
                        return;

                    float offhandSpeed = Math.clamp(payload.offhandSpeed(), 0f, MAX_SYNC_SPEED);
                    float mainhandSpeed = Math.clamp(payload.mainhandSpeed(), 0f, MAX_SYNC_SPEED);

                    if (offhandSpeed < FULL_SPEED_THRESHOLD)
                        return;

                    if (mainhandSpeed < FULL_SPEED_THRESHOLD)
                        return;

                    CreatorSwordCriteriaTriggers.FULL_SPEED.get().trigger(player);
                }
        );

        registrar.playToServer(
                ShieldThrowPayload.TYPE,
                ShieldThrowPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);

                    if (!(offhand.getItem() instanceof CogwheelShieldItem))
                        return;

                    CompoundTag persistentData = player.getPersistentData();

                    if (persistentData.contains(THROWN_SHIELD_TAG))
                        return;

                    if (hasExistingThrownShield(player))
                        return;

                    float speed = Math.clamp(payload.speed(), 0f, MAX_SYNC_SPEED);

                    if (speed < THROW_SPEED_THRESHOLD)
                        return;

                    offhand.set(GEAR_SHIELD_SPEED.get(), speed);
                    offhand.set(GEAR_SHIELD_CHARGING.get(), false);
                    offhand.set(GEAR_SHIELD_DECAYING.get(), true);

                    persistentData.put(THROWN_SHIELD_TAG, offhand.save(player.registryAccess()));

                    ThrownCogwheelShield projectile =
                            new ThrownCogwheelShield(player.level(), player, speed, offhand.copy());

                    projectile.shootFromRotation(
                            player,
                            player.getXRot(),
                            player.getYRot(),
                            0.0F,
                            1.5F,
                            1.0F
                    );

                    player.level().addFreshEntity(projectile);
                    player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
        );

        registrar.playToServer(
                ShieldStatePayload.TYPE,
                ShieldStatePayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    InteractionHand hand = payload.offhand()
                            ? InteractionHand.OFF_HAND
                            : InteractionHand.MAIN_HAND;

                    ItemStack serverStack = player.getItemInHand(hand);

                    if (!(serverStack.getItem() instanceof CogwheelShieldItem))
                        return;

                    float speed = Math.clamp(payload.speed(), 0f, MAX_SYNC_SPEED);

                    float oldSpeed = serverStack.getOrDefault(GEAR_SHIELD_SPEED.get(), 0f);
                    boolean oldCharging = serverStack.getOrDefault(GEAR_SHIELD_CHARGING.get(), false);
                    boolean oldDecaying = serverStack.getOrDefault(GEAR_SHIELD_DECAYING.get(), false);

                    if (Math.abs(oldSpeed - speed) > 0.01f) {
                        serverStack.set(GEAR_SHIELD_SPEED.get(), speed);
                    }

                    if (oldCharging != payload.charging()) {
                        serverStack.set(GEAR_SHIELD_CHARGING.get(), payload.charging());
                    }

                    if (oldDecaying != payload.decaying()) {
                        serverStack.set(GEAR_SHIELD_DECAYING.get(), payload.decaying());
                    }
                }
        );
    }

    private static boolean hasExistingThrownShield(ServerPlayer player) {
        return !player.level().getEntitiesOfClass(
                ThrownCogwheelShield.class,
                player.getBoundingBox().inflate(5),
                entity -> entity.getOwner() == player
        ).isEmpty();
    }
}