package com.erix.creatorsword;

import com.erix.creatorsword.datagen.CSDatapackBuiltinEntriesProvider;
import com.erix.creatorsword.datagen.advancements.CSAdvancementProvider;
import com.erix.creatorsword.datagen.recipes.CSMixingRecipeGen;
import com.erix.creatorsword.datagen.recipes.CSRecipe;
import com.erix.creatorsword.datagen.tags.ModEnchantmentTag;
import com.erix.creatorsword.datagen.tags.ModItemTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class CSDataGenerator {
    public static void register(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTags = CompletableFuture.completedFuture(null);
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CSDatapackBuiltinEntriesProvider builtin = new CSDatapackBuiltinEntriesProvider(output, lookupProvider);
        generator.addProvider(event.includeServer(), builtin);
        CompletableFuture<HolderLookup.Provider> registryProvider = builtin.getRegistryProvider();

        generator.addProvider(event.includeServer(), new CSRecipe(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CSMixingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ModItemTag(output, lookupProvider, blockTags, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModEnchantmentTag(output, registryProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new CSAdvancementProvider(output, lookupProvider, existingFileHelper));
    }
}