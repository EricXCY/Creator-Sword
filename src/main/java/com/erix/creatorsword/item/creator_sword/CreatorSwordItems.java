package com.erix.creatorsword.item.creator_sword;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreatorSwordItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredItem<SwordItem> CREATOR_SWORD = ITEMS.registerItem("creator_sword",
            CreatorSwordItem::new);
    public static final DeferredItem<SwordItem> NETHERITE_CREATOR_SWORD = ITEMS.registerItem("netherite_creator_sword",
            NetheriteCreatorSwordItem::new);
}
