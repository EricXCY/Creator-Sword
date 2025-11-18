package com.erix.creatorsword.enchantment;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.erix.creatorsword.item.creator_sword.BaseCreatorSwordItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreatorSword.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PneumaticBoostAttackSpeedHandler {

    // 1.21 要用 ResourceLocation 作为 modifier ID
    private static final ResourceLocation ATTACK_SPEED_MODIFIER =
            ResourceLocation.fromNamespaceAndPath("creatorsword", "pneumatic_boost_attack_speed");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide())
            return;

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof BaseCreatorSwordItem)) {
            removeModifier(player);
            return;
        }

        int level = EnchantmentKeys.getEnchantmentLevel(
                player.level().registryAccess(),
                EnchantmentKeys.PNEUMATIC_BOOST,
                stack
        );

        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attr == null) return;

        if (level <= 0) {
            removeModifier(player);
            return;
        }

        double bonus = 0.2 * level;

        AttributeModifier existing = attr.getModifier(ATTACK_SPEED_MODIFIER);

        if (existing != null && existing.amount() == bonus) {
            return;
        }

        if (existing != null) {
            attr.removeModifier(existing);
        }

        attr.addPermanentModifier(
                new AttributeModifier(
                        ATTACK_SPEED_MODIFIER,
                        bonus,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }

    private static void removeModifier(Player player) {
        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attr == null) return;

        AttributeModifier existing = attr.getModifier(ATTACK_SPEED_MODIFIER);
        if (existing != null) {
            attr.removeModifier(existing);
        }
    }
}
