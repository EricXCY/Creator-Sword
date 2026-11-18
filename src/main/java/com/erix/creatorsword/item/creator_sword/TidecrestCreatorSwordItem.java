package com.erix.creatorsword.item.creator_sword;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

public class TidecrestCreatorSwordItem extends BaseCreatorSwordItem {
    public TidecrestCreatorSwordItem(Properties properties) {
        super(Tiers.NETHERITE, properties.fireResistant().durability(2031).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 6, -1.5f)).rarity(Rarity.EPIC));
    }

    @Override
    public ResourceLocation getSwordModelLocation() {
        return CreatorSwordItems.TIDECREST_CREATOR_SWORD_MODEL;
    }

    @Override
    public ResourceLocation getGearModelLocation() {
        return CreatorSwordItems.TIDECREST_CREATOR_SWORD_GEAR_MODEL;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.NETHERITE_INGOT);
    }
}