package com.erix.creatorsword.item.creator_sword;

import java.util.function.Consumer;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CreatorSwordItem extends WrenchItem {
    public CreatorSwordItem(Properties properties) {
        super(new Properties().durability(1561).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3.1f, -2.3f)));
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CreatorSwordItemRenderer()));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return Tiers.DIAMOND.getEnchantmentValue();
    }
}
