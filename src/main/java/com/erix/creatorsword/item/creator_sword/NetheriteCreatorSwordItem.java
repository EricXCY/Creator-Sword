package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.world.item.*;

public class NetheriteCreatorSwordItem extends BaseCreatorSwordItem {
    public NetheriteCreatorSwordItem(Properties properties) {
        super(Tiers.NETHERITE, new Properties().fireResistant().durability(2031).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4.2f, -2.3f)));
    }

    @Override
    protected CustomRenderedItemModelRenderer getRenderer() {
        return new NetheriteCreatorSwordItemRenderer();
    }
}