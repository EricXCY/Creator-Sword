package com.erix.creatorsword;

import com.erix.creatorsword.datagen.recipes.ModRecipe;
import com.erix.creatorsword.datagen.tags.ModTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = CreatorSword.MODID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void register(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTags = CompletableFuture.completedFuture(null);
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(),new ModRecipe(output,lookupProvider));
        generator.addProvider(event.includeServer(),new ModTag(output, lookupProvider, blockTags, existingFileHelper));
    }
}
