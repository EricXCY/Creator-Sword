package com.erix.creatorsword.datagen.recipes;

import com.erix.creatorsword.item.cogwheel_shield.CogwheelshieldItems;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModRecipe extends RecipeProvider {
    public ModRecipe(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) { // 有序合成
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CogwheelshieldItems.COGWHEEL_SHIELD.get())
                .pattern("PSP")
                .pattern("PPP")
                .pattern(" P ")
                .define('P', ItemTags.PLANKS)
                .define('S', AllBlocks.SHAFT.get())
                .unlockedBy("has_andesite",has(AllBlocks.SHAFT))
                .save(recipeOutput);
    }
}
