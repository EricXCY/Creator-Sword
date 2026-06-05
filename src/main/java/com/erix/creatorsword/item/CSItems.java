package com.erix.creatorsword.item;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CSItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredItem<Item> TRIAL_KEY_DIE = ingredient("trial_key_die");
    public static final DeferredItem<Item> INCOMPLETE_TRIAL_KEY_DIE = ingredient("incomplete_trial_key_die");
    public static final DeferredItem<Item> OMINOUS_TRIAL_KEY_DIE = ingredient("ominous_trial_key_die");
    public static final DeferredItem<Item> INCOMPLETE_OMINOUS_TRIAL_KEY_DIE = ingredient("incomplete_ominous_trial_key_die");
    public static final DeferredItem<Item> PRECISION_TRIAL_KEY = ingredient("precision_trial_key");
    public static final DeferredItem<Item> PRECISION_OMINOUS_TRIAL_KEY = ingredient("precision_ominous_trial_key");


    private static DeferredItem<Item> ingredient(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}