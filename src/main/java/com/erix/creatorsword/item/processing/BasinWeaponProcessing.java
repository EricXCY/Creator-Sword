package com.erix.creatorsword.item.processing;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.Comparator;

final class BasinWeaponProcessing {
    private static final int MAX_BASIN_OPERATIONS = 256;
    private BasinWeaponProcessing() {}

    static boolean process(BasinBlockEntity basin, boolean execute) {
        boolean changed = false;
        var recipe = basin.getLevel().getRecipeManager().getRecipes().stream()
                .filter(BasinWeaponProcessing::isBasinPressRecipe)
                .sorted(Comparator.<RecipeHolder<?>>comparingInt(r -> -r.value().getIngredients().size())
                        .thenComparing(r -> r.id().toString()))
                .filter(r -> BasinRecipe.match(basin, r.value())).findFirst();
        if (recipe.isEmpty()) return false;
        if (!execute) return true;
        for (int i = 0; i < MAX_BASIN_OPERATIONS; i++) {
            if (!BasinRecipe.match(basin, recipe.get().value())
                    || !BasinRecipe.apply(basin, recipe.get().value())) break;
            changed = true;
        }
        if (changed) {
            basin.notifyChangeOfContents();
            basin.notifyUpdate();
        }
        return changed;
    }

    private static boolean isBasinPressRecipe(RecipeHolder<?> holder) {
        Recipe<?> recipe = holder.value();
        return recipe.getType() == AllRecipeTypes.COMPACTING.getType()
                || recipe instanceof CraftingRecipe && !(recipe instanceof MechanicalCraftingRecipe)
                && MechanicalPressBlockEntity.canCompress(recipe) && !AllRecipeTypes.shouldIgnoreInAutomation(holder);
    }
}
