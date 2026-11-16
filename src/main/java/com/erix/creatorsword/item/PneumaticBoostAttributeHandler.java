package com.erix.creatorsword.item;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.erix.creatorsword.item.creator_sword.BaseCreatorSwordItem;
import com.erix.creatorsword.item.flywheel_mace.FlywheelMaceItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CreatorSword.MODID)
public final class PneumaticBoostAttributeHandler {
    private static final ResourceLocation ATTACK_SPEED = CreatorSword.asResource("pneumatic_boost_effect_attack_speed");
    private static final ResourceLocation LEGACY_ATTACK_SPEED = CreatorSword.asResource("pneumatic_boost_attack_speed");

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        var stack = event.getItemStack();
        if (!(stack.getItem() instanceof BaseCreatorSwordItem)
                && !(stack.getItem() instanceof FlywheelMaceItem)) return;

        for (var entry : stack.getTagEnchantments().entrySet()) {
            if (entry.getKey().is(EnchantmentKeys.PNEUMATIC_BOOST) && entry.getIntValue() > 0) {
                event.addModifier(Attributes.ATTACK_SPEED,
                        new AttributeModifier(ATTACK_SPEED, 0.2 * entry.getIntValue(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND);
                break;
            }
        }
    }

    //清理旧属性修饰符ID，后续版本删除
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        var attribute = event.getEntity().getAttribute(Attributes.ATTACK_SPEED);
        if (attribute != null) attribute.removeModifier(LEGACY_ATTACK_SPEED);
    }
}
