package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CreatorSwordItem extends BaseCreatorSwordItem {
    public CreatorSwordItem(Properties properties) {
        super(Tiers.DIAMOND, new Properties().durability(1561).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 4, -2.3f)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected CustomRenderedItemModelRenderer getRenderer() {
        return new CreatorSwordItemRenderer();
    }
}