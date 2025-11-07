package com.erix.creatorsword.item.incomplete_enchantment_book;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IncompleteEnchantmentBookItems extends Item {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);
    public IncompleteEnchantmentBookItems(Properties properties) {
        super(properties);
    }
    public static final DeferredItem<com.erix.creatorsword.item.incomplete_enchantment_book.IncompleteEnchantmentBookItems> INCOMPLETE_OVERDRIVE = ITEMS.registerItem("incomplete_overdrive_book",
            com.erix.creatorsword.item.incomplete_enchantment_book.IncompleteEnchantmentBookItems::new);

    public static final DeferredItem<com.erix.creatorsword.item.incomplete_enchantment_book.IncompleteEnchantmentBookItems> INCOMPLETE_PNEUMATIC_BOOST = ITEMS.registerItem("incomplete_pneumatic_boost",
            com.erix.creatorsword.item.incomplete_enchantment_book.IncompleteEnchantmentBookItems::new);
}

