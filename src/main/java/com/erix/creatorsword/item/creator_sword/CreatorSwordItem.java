package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.AllItems;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

public class CreatorSwordItem extends BaseCreatorSwordItem {
    public CreatorSwordItem(Properties properties) {
        super(
                Tiers.DIAMOND,
                properties.durability(1561)
                        .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 4, -2.3f))
        );
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(AllItems.BRASS_SHEET);
    }
}