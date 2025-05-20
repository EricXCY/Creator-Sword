//
// Functions of wrench comes from simibubi [https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/content/equipment/wrench/WrenchItem.java]
//

package com.erix.creatorsword.item.incomplete_creator_sword;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class IncompleteNetheriteCreatorSwordItem extends WrenchItem {

    public IncompleteNetheriteCreatorSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new IncompleteNetheriteCreatorSwordItemRenderer()));
    }
}
