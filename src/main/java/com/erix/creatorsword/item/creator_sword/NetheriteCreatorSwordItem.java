package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class NetheriteCreatorSwordItem extends BaseCreatorSwordItem {
    public NetheriteCreatorSwordItem(Properties properties) {
        super(Tiers.NETHERITE, new Properties().fireResistant().durability(2031).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 5, -2.3f)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected CustomRenderedItemModelRenderer getRenderer() {
        return new NetheriteCreatorSwordItemRenderer();
    }
}