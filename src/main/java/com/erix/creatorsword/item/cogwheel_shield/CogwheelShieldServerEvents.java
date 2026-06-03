package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import static com.erix.creatorsword.data.CSDataComponents.GEAR_SHIELD_CHARGING;
import static com.erix.creatorsword.data.CSDataComponents.GEAR_SHIELD_DECAYING;
import static com.erix.creatorsword.data.CSDataComponents.GEAR_SHIELD_LAST_UPDATE;
import static com.erix.creatorsword.data.CSDataComponents.GEAR_SHIELD_SPEED;

@EventBusSubscriber(modid = CreatorSword.MODID)
public class CogwheelShieldServerEvents {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long tick = event.getServer().overworld().getGameTime();

        if (tick % CogwheelShieldItem.UPDATE_INTERVAL_TICKS != 0)
            return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!CogwheelShieldChargingManager.isActive(player))
                continue;

            ItemStack stack = getHeldCogwheelShield(player);

            if (stack.isEmpty()) {
                CogwheelShieldChargingManager.remove(player);
                continue;
            }

            if (!(stack.getItem() instanceof CogwheelShieldItem shield)) {
                CogwheelShieldChargingManager.remove(player);
                continue;
            }

            if (CogwheelShieldChargingManager.isCharging(player)) {
                shield.serverTickAcceleration(stack, player, tick);
            } else if (CogwheelShieldChargingManager.isDecaying(player)) {
                serverTickDecay(stack, player, tick);
            }
        }
    }

    private static void serverTickDecay(ItemStack stack, ServerPlayer player, long tick) {
        float speed = CogwheelShieldChargingManager.getSpeed(player);

        speed *= CogwheelShieldItem.DECAY_RATE;

        if (speed < 1f) {
            stack.set(GEAR_SHIELD_SPEED.get(), 0f);
            stack.set(GEAR_SHIELD_CHARGING.get(), false);
            stack.set(GEAR_SHIELD_DECAYING.get(), false);
            stack.set(GEAR_SHIELD_LAST_UPDATE.get(), tick);

            CogwheelShieldChargingManager.remove(player);
            return;
        }

        CogwheelShieldChargingManager.setSpeed(player, speed);
    }

    private static ItemStack getHeldCogwheelShield(ServerPlayer player) {
        ItemStack offhand = player.getOffhandItem();

        if (offhand.getItem() instanceof CogwheelShieldItem)
            return offhand;

        ItemStack mainhand = player.getMainHandItem();

        if (mainhand.getItem() instanceof CogwheelShieldItem)
            return mainhand;

        return ItemStack.EMPTY;
    }
}