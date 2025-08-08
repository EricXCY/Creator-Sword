package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.world.item.*;

public class CreatorSwordItem extends BaseCreatorSwordItem {
    public CreatorSwordItem(Properties properties) {
        super(Tiers.DIAMOND, new Properties().durability(1561).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3.1f, -2.3f)));
    }

    @Override
    protected CustomRenderedItemModelRenderer getRenderer() {
        return new CreatorSwordItemRenderer();
    }
}