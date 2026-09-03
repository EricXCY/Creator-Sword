package com.erix.creatorsword.item.flywheel_mace;

import com.erix.creatorsword.CreatorSword;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FlywheelMaceItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);
    public static final DeferredItem<FlywheelMaceItem> FLYWHEEL_MACE =
            ITEMS.registerItem("flywheel_mace", FlywheelMaceItem::new);
}
