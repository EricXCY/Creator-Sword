package com.erix.creatorsword.item.supreme_glue;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SupremeGlueItem extends SuperGlueItem {
    public SupremeGlueItem(Properties properties) {
        super(properties);
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);
    public static final DeferredItem<SupremeGlueItem> SUPREME_GLUE = ITEMS.registerItem("supreme_glue",
            SupremeGlueItem::new);
}
