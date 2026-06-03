package com.erix.creatorsword.item.cogwheel_shield.logic;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.data.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.item.cogwheel_shield.BaseCogwheelShieldItem;
import com.erix.creatorsword.item.cogwheel_shield.BaseCogwheelShieldEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CogwheelShieldNetwork {
    private static final String THROWN_SHIELD_TAG = "creatorsword_thrown_shield";

    private static final float FULL_SPEED_THRESHOLD = 256f;
    private static final float MAX_SYNC_SPEED = 512f;

    private CogwheelShieldNetwork() {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registerCharging(registrar);
        registerThrow(registrar);
        registerFullSpeed(registrar);
    }

    private static void registerCharging(PayloadRegistrar registrar) {
        registrar.playToServer(
                ShieldChargingPayload.TYPE,
                ShieldChargingPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    ItemStack stack = CogwheelShieldUtil.getHeldCogwheelShield(player);

                    if (!(stack.getItem() instanceof BaseCogwheelShieldItem))
                        return;

                    if (payload.charging()) {
                        float initialSpeed = CogwheelShieldUtil.getServerOrStackSpeed(player, stack);
                        CogwheelShieldStateManager.startCharging(player, initialSpeed);
                    } else {
                        CogwheelShieldStateManager.stopCharging(player);
                    }
                }
        );
    }

    private static void registerThrow(PayloadRegistrar registrar) {
        registrar.playToServer(
                ShieldThrowPayload.TYPE,
                ShieldThrowPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    ItemStack stack = CogwheelShieldUtil.getOffhandCogwheelShield(player);

                    if (!(stack.getItem() instanceof BaseCogwheelShieldItem shield))
                        return;

                    if (!shield.canThrowFromHand(stack, player, InteractionHand.OFF_HAND))
                        return;

                    CompoundTag persistentData = player.getPersistentData();

                    if (persistentData.contains(THROWN_SHIELD_TAG))
                        return;

                    if (hasExistingThrownShield(player))
                        return;

                    float speed = CogwheelShieldUtil.getServerOrStackSpeed(player, stack);
                    speed = Math.clamp(speed, 0f, shield.getMaxSpeed(stack, player));

                    if (speed < shield.getThrowSpeedThreshold(stack, player))
                        return;

                    stack.set(CSDataComponents.GEAR_SHIELD_SPEED.get(), speed);
                    stack.set(CSDataComponents.GEAR_SHIELD_CHARGING.get(), false);
                    stack.set(CSDataComponents.GEAR_SHIELD_DECAYING.get(), true);

                    persistentData.put(THROWN_SHIELD_TAG, stack.save(player.registryAccess()));

                    BaseCogwheelShieldEntity projectile =
                            shield.createThrownEntity(player.level(), player, speed, stack.copy());

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

                    shield.onThrown(player, stack, speed);
                    CogwheelShieldStateManager.remove(player);
                }
        );
    }

    private static void registerFullSpeed(PayloadRegistrar registrar) {
        registrar.playToServer(
                ShieldFullSpeedPayload.TYPE,
                ShieldFullSpeedPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();

                    ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                    ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);

                    if (!(offhand.getItem() instanceof BaseCogwheelShieldItem))
                        return;

                    if (!(mainhand.getItem() instanceof BaseCogwheelShieldItem))
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
    }

    private static boolean hasExistingThrownShield(ServerPlayer player) {
        return !player.level().getEntitiesOfClass(
                BaseCogwheelShieldEntity.class,
                player.getBoundingBox().inflate(5),
                entity -> entity.getOwner() == player
        ).isEmpty();
    }
}