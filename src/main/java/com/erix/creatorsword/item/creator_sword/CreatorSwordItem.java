package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class CreatorSwordItem extends BaseCreatorSwordItem {
    public CreatorSwordItem(Properties properties) {
        super(Tiers.DIAMOND, properties.durability(1561).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 4, -2.3f)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected CustomRenderedItemModelRenderer getRenderer() {
        return new CreatorSwordItemRenderer();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(AllItems.BRASS_SHEET);
    }
}