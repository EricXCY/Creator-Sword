package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class CogwheelShieldItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredItem<Item> COGWHEEL_SHIELD = ITEMS.registerItem("cogwheel_shield",
            CogwheelShieldItem::new);
}