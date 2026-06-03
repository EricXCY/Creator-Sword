package com.erix.creatorsword.item.creator_sword;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TrialCreatorSwordItem extends BaseCreatorSwordItem {
    public TrialCreatorSwordItem(Properties properties) {
        super(Tiers.NETHERITE, properties.fireResistant().durability(2031).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 6, -1.8f)).rarity(Rarity.EPIC));
    }

    @Override
    public ResourceLocation getSwordModelLocation() {
        return CreatorSwordItems.TRIAL_CREATOR_SWORD_MODEL;
    }

    @Override
    public ResourceLocation getGearModelLocation() {
        return CreatorSwordItems.TRIAL_CREATOR_SWORD_GEAR_MODEL;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.NETHERITE_INGOT);
    }
}