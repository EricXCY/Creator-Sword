package com.erix.creatorsword.datagen.recipes;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.fluid.ominous.OminousEssenceHelper;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.concurrent.CompletableFuture;

public class CSMixingRecipeGen extends MixingRecipeGen {

    public CSMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreatorSword.MODID);
    }

    GeneratedRecipe OMINOUS_ESSENCE = create("ominous_essence_from_oozing_and_slowness", b -> b
            .require(sized(potionFluid(Potions.OOZING)))
            .require(sized(potionFluid(Potions.SLOWNESS)))
            .output(OminousEssenceHelper.create(250, 0))
            .requiresHeat(HeatCondition.HEATED)
    );

    GeneratedRecipe OMINOUS_ESSENCE_II = create("ominous_essence_ii_from_ominous_essence_and_infested", b -> b
            .require(sized(OminousEssenceHelper.create(250, 0)))
            .require(sized(potionFluid(Potions.INFESTED)))
            .output(OminousEssenceHelper.create(250, 1))
            .requiresHeat(HeatCondition.HEATED)
    );

    GeneratedRecipe OMINOUS_ESSENCE_III = create("ominous_essence_iii_from_ominous_essence_ii_and_weakness", b -> b
            .require(sized(OminousEssenceHelper.create(250, 1)))
            .require(sized(potionFluid(Potions.WEAKNESS)))
            .output(OminousEssenceHelper.create(250, 2))
            .requiresHeat(HeatCondition.SUPERHEATED)
    );

    GeneratedRecipe OMINOUS_ESSENCE_IV = create("ominous_essence_iv_from_ominous_essence_iii_and_poison", b -> b
            .require(sized(OminousEssenceHelper.create(250, 2)))
            .require(sized(potionFluid(Potions.POISON)))
            .output(OminousEssenceHelper.create(250, 3))
            .requiresHeat(HeatCondition.SUPERHEATED)
    );

    GeneratedRecipe OMINOUS_ESSENCE_V = create("ominous_essence_v_from_ominous_essence_iv_and_harming", b -> b
            .require(sized(OminousEssenceHelper.create(250, 3)))
            .require(sized(potionFluid(Potions.HARMING)))
            .output(OminousEssenceHelper.create(250, 4))
            .requiresHeat(HeatCondition.SUPERHEATED)
    );

    private static SizedFluidIngredient sized(FluidStack stack) {
        return new SizedFluidIngredient(
                DataComponentFluidIngredient.of(false, stack),
                stack.getAmount()
        );
    }

    private static FluidStack potionFluid(Holder<Potion> potion) {
        return PotionFluid.of(
                250,
                new PotionContents(potion),
                PotionFluid.BottleType.REGULAR
        );
    }
}