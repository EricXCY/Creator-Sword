package com.erix.creatorsword.item.incomplete_creator_sword;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IncompleteItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredItem<WrenchItem> INCOMPLETE_CREATOR_SWORD = ITEMS.registerItem("incomplete_creator_sword",
            IncompleteCreatorSwordItem::new);
    public static final DeferredItem<WrenchItem> INCOMPLETE_NETHERITE_CREATOR_SWORD = ITEMS.registerItem("incomplete_netherite_creator_sword",
            IncompleteNetheriteCreatorSwordItem::new);
}
