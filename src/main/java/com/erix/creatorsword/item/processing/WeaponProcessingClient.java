package com.erix.creatorsword.item.processing;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CreatorSword.MODID, value = Dist.CLIENT)
public final class WeaponProcessingClient {
    private static Player owner;
    private static Item held;
    private static int selectedSlot;
    private static boolean attack;

    private WeaponProcessingClient() {}

    private static void resetIfReleased() {
        var mc = Minecraft.getInstance();
        if (mc.player != owner || mc.screen != null || owner == null
                || owner.getMainHandItem().getItem() != held || owner.getInventory().selected != selectedSlot
                || !(attack ? mc.options.keyAttack : mc.options.keyUse).isDown()) {
            owner = null;
            held = null;
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) { resetIfReleased(); }

    @SubscribeEvent
    public static void preventMining(PlayerInteractEvent.LeftClickBlock event) {
        resetIfReleased();
        if (event.getLevel().isClientSide && event.getEntity() == owner && attack)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void interact(InputEvent.InteractionKeyMappingTriggered event) {
        resetIfReleased();
        var player = Minecraft.getInstance().player;
        if (player == null || (!event.isAttack() && !event.isUseItem())) return;
        if (owner != null && attack == event.isAttack()) {
            event.setCanceled(true);
            event.setSwingHand(false);
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        var kind = ProcessingKind.of(player.getMainHandItem());
        if (kind == null || kind.usesAttack() != event.isAttack()
                || !WeaponRecipeProcessing.canProcess(player)) return;
        owner = player;
        held = player.getMainHandItem().getItem();
        selectedSlot = player.getInventory().selected;
        attack = event.isAttack();
        event.setCanceled(true);
        event.setSwingHand(true);
        PacketDistributor.sendToServer(new WeaponProcessingNetwork.Process(attack));
    }
}
