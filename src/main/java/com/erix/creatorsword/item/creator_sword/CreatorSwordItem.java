package com.erix.creatorsword.item.creator_sword;

import java.util.function.Consumer;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CreatorSwordItem extends SwordItem {

    public CreatorSwordItem(Properties properties) {
        super(Tiers.DIAMOND, new Properties().attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3.1f, -2.3f)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CreatorSwordItemRenderer()));
    }
}
