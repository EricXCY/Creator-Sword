package com.erix.creatorsword.item.smithing_template;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SmithingTemplateItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredItem<Item> CRIMSON_AFTERGLOW_SMITHING_TEMPLATE =
            ITEMS.register(
                    "crimson_afterglow_smithing_template",
                    CSmithingTemplateItem::createCrimsonAfterglowTemplate
            );

    public static final DeferredItem<Item> TRIAL_SMITHING_TEMPLATE =
            ITEMS.register(
                    "trial_smithing_template",
                    CSmithingTemplateItem::createTrialTemplate
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}