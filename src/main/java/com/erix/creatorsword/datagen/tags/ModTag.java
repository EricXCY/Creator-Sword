package com.erix.creatorsword.datagen.tags;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelshieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModTag extends ItemTagsProvider {
    public ModTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                  CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreatorSword.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.SWORDS)
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());
        tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());
        tag(Tags.Items.TOOLS_WRENCH)
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());

        tag(Tags.Items.TOOLS_SHIELD)
                .add(CogwheelshieldItems.COGWHEEL_SHIELD.get());
        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(CogwheelshieldItems.COGWHEEL_SHIELD.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(CogwheelshieldItems.COGWHEEL_SHIELD.get());
    }
}
