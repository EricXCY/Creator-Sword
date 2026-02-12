package com.erix.creatorsword.datagen.tags;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTag extends EnchantmentTagsProvider {
    public ModEnchantmentTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreatorSword.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(EnchantmentTags.TREASURE)
                .add(EnchantmentKeys.OVERDRIVE)
                .add(EnchantmentKeys.PNEUMATIC_BOOST)
                .add(EnchantmentKeys.STURDY)
                .add(EnchantmentKeys.STICKY_TONGUE);
    }
}