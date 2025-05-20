package com.erix.creatorsword.datagen.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public class EnchantedSequencedAssemblyRecipe extends SequencedAssemblyRecipe {

    protected Ingredient ingredient;
    protected List<SequencedRecipe<?>> sequence;
    protected int loops;
    protected ProcessingOutput transitionalItem;


    public EnchantedSequencedAssemblyRecipe(SequencedAssemblyRecipeSerializer serializer) {
        super(serializer);
    }

    // 复制所有 NBT 数据
    private ItemStack copyDataComponents(ItemStack source, ItemStack target) {
        target.applyComponents(source.getComponents());
        return target;
    }

    // 保留附魔和 NBT 数据
    private ItemStack preserveEnchantmentsAndNBT(ItemStack input, ItemStack output) {
        output.applyComponents(input.getComponents());
        return output;
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider registries) {
        ItemStack input = inv.getItem(0);
        ItemStack result = super.assemble(inv, registries);
        return preserveEnchantmentsAndNBT(input, result);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack result = super.getResultItem(registries);
        return preserveEnchantmentsAndNBT(result, result.copy());
    }

    @Override
    public ItemStack getTransitionalItem() {
        ItemStack transitional = super.getTransitionalItem();
        return transitional.copy();
    }
}