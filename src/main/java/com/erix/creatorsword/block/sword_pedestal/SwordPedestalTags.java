package com.erix.creatorsword.block.sword_pedestal;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SwordPedestalTags {
    public static final TagKey<Item> LOWEST = tier("lowest");
    public static final TagKey<Item> LOW = tier("low");
    public static final TagKey<Item> MEDIUM = tier("medium");
    public static final TagKey<Item> HIGH = tier("high");
    public static final TagKey<Item> HIGHEST = tier("highest");

    public static int capacityFor(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(HIGH)) return 32768;
        if (stack.is(MEDIUM)) return 16384;
        if (stack.is(LOW)) return 8192;
        if (stack.is(LOWEST)) return 4096;
        return 0;
    }

    private static TagKey<Item> tier(String name) {
        return TagKey.create(Registries.ITEM, CreatorSword.asResource("sword_pedestal/" + name));
    }
    private SwordPedestalTags() {}
}
