package com.erix.creatorsword.datagen.recipes;

import com.erix.creatorsword.item.capture_box.CaptureBoxItem;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipe extends RecipeProvider {
    public ModRecipe(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) { // 有序合成
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CogwheelShieldItems.COGWHEEL_SHIELD.get())
                .pattern("PSP")
                .pattern("PPP")
                .pattern(" P ")
                .define('P', ItemTags.PLANKS)
                .define('S', AllBlocks.SHAFT.get())
                .unlockedBy("has_andesite",has(AllBlocks.SHAFT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FrogportGrappleItem.FROGPORT_GRAPPLE.get())
                .pattern(" G ")
                .pattern(" F ")
                .pattern("SBS")
                .define('G', AllItems.GOGGLES)
                .define('F', AllBlocks.PACKAGE_FROGPORT.get())
                .define('S', Items.STRING)
                .define('B', Items.SLIME_BALL)
                .unlockedBy("has_package_frogport",has(AllBlocks.PACKAGE_FROGPORT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CaptureBoxItem.CAPTURE_BOX.get())
                .pattern(" C ")
                .pattern(" B ")
                .pattern(" C ")
                .define('C', AllItems.CARDBOARD)
                .define('B', Items.BARREL)
                .unlockedBy("has_barrel",has(Items.BARREL))
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
