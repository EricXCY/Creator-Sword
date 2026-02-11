package com.erix.creatorsword.event;

import com.erix.creatorsword.entity.ThrownCogwheelShield;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ShieldRecoveryEvents {
    private static final String TAG = "creatorsword_thrown_shield";

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            recover(sp, true);
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            recover(sp, false);
        }
    }

    private static void recover(ServerPlayer player, boolean forceKillEntity) {
        boolean hasEntity = false;

        for (ServerLevel lvl : player.server.getAllLevels()) {
            for (Entity e : lvl.getAllEntities()) {
                if (e instanceof ThrownCogwheelShield shield && shield.getOwner() == player) {
                    hasEntity = true;
                    if (forceKillEntity) shield.discard();
                }
            }
        }

        if (hasEntity && !forceKillEntity) return;

        CompoundTag pd = player.getPersistentData();
        if (!pd.contains(TAG)) return;

        ItemStack stack = ItemStack.parseOptional(player.registryAccess(), pd.getCompound(TAG));
        pd.remove(TAG);

        if (stack.isEmpty()) return;

        if (player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
            player.setItemInHand(InteractionHand.OFF_HAND, stack);
        } else if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        } else {
            player.inventoryMenu.broadcastChanges();
        }
    }
}
