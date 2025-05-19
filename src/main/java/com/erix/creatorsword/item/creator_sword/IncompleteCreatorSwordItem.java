//
// Functions of wrench comes from simibubi [https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/content/equipment/wrench/WrenchItem.java]
//

package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class IncompleteCreatorSwordItem extends Item {

    public IncompleteCreatorSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new IncompleteCreatorSwordItemRenderer()));
    }
}
