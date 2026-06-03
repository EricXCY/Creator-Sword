package com.erix.creatorsword.item.cogwheel_shield.logic;

import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.item.cogwheel_shield.BaseCogwheelShieldItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CogwheelShieldUtil {
    private CogwheelShieldUtil() {
    }

    public static boolean isCogwheelShield(ItemStack stack) {
        return stack.getItem() instanceof BaseCogwheelShieldItem;
    }

    public static ItemStack getHeldCogwheelShield(Player player) {
        ItemStack offhand = player.getOffhandItem();

        if (isCogwheelShield(offhand))
            return offhand;

        ItemStack mainhand = player.getMainHandItem();

        if (isCogwheelShield(mainhand))
            return mainhand;

        return ItemStack.EMPTY;
    }

    public static ItemStack getOffhandCogwheelShield(Player player) {
        ItemStack offhand = player.getOffhandItem();

        if (isCogwheelShield(offhand))
            return offhand;

        return ItemStack.EMPTY;
    }

    public static float getServerOrStackSpeed(ServerPlayer player, ItemStack stack) {
        CogwheelShieldStateManager.State state = CogwheelShieldStateManager.get(player);

        if (state != null)
            return state.speed;

        return stack.getOrDefault(CSDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
    }
}