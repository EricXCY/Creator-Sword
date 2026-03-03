package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class CNYCreatorSwordItem extends BaseCreatorSwordItem {
    public CNYCreatorSwordItem(Properties properties) {
        super(Tiers.NETHERITE, properties.fireResistant().durability(2031).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 6, -2.1f)).rarity(Rarity.EPIC));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected CustomRenderedItemModelRenderer getRenderer() {
        return new CNYCreatorSwordItemRenderer();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.NETHERITE_INGOT);
    }
}