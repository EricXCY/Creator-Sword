package com.erix.creatorsword.datagen.recipes;

import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipe extends RecipeProvider {
    public ModRecipe(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) { // 有序合成
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CogwheelShieldItems.COGWHEEL_SHIELD.get())
                .pattern("PSP")
                .pattern("PPP")
                .pattern(" P ")
                .define('P', ItemTags.PLANKS)
                .define('S', AllBlocks.SHAFT.get())
                .unlockedBy("has_andesite",has(AllBlocks.SHAFT))
                .save(recipeOutput);
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(CreatorSwordItems.CREATOR_SWORD.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.COMBAT, CreatorSwordItems.NETHERITE_CREATOR_SWORD.get()
                )
                .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput, "netherite_creator_sword_smithing");
    }
}
