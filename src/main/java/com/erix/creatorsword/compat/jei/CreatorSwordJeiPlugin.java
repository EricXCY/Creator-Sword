package com.erix.creatorsword.compat.jei;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.fluid.ominous.OminousEssenceHelper;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.flywheel_mace.FlywheelMaceItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CreatorSwordJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = CreatorSword.asResource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerExtraIngredients(@NotNull IExtraIngredientRegistration registration) {
        List<FluidStack> fluids = new ArrayList<>();
        for (int amplifier = 0; amplifier <= 4; amplifier++) {
            fluids.add(OminousEssenceHelper.create(1000, amplifier));
        }
        registration.addExtraIngredients(NeoForgeTypes.FLUID_STACK, fluids);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IVanillaRecipeFactory vanilla = registration.getVanillaRecipeFactory();
        registration.addRecipes(RecipeTypes.ANVIL, List.of(
                makeAnvilRepair(vanilla, CogwheelShieldItems.COGWHEEL_SHIELD.get(), AllBlocks.SHAFT.asItem()),
                makeAnvilRepair(vanilla, CreatorSwordItems.CREATOR_SWORD.get(), AllItems.BRASS_SHEET.asItem()),
                makeAnvilRepair(vanilla, CreatorSwordItems.NETHERITE_CREATOR_SWORD.get(), Items.NETHERITE_INGOT),
                makeAnvilRepair(vanilla, CreatorSwordItems.CNY_CREATOR_SWORD.get(), Items.NETHERITE_INGOT),
                makeAnvilRepair(vanilla, CreatorSwordItems.TRIAL_CREATOR_SWORD.get(), Items.NETHERITE_INGOT),
                makeAnvilRepair(vanilla, FlywheelMaceItem.FLYWHEEL_MACE.get(), Items.HEAVY_CORE)
        ));
    }

    private static IJeiAnvilRecipe makeAnvilRepair(
            IVanillaRecipeFactory vanilla, ItemLike item, ItemLike repairMaterial
    ) {
        ItemStack damaged = new ItemStack(item);
        damaged.setDamageValue(damaged.getMaxDamage() - 1);

        ItemStack repaired = damaged.copy();
        int repair = damaged.getMaxDamage() / 4;
        repaired.setDamageValue(Math.max(0, damaged.getDamageValue() - repair));

        ResourceLocation uid = CreatorSword.asResource(
                "anvil_repair/" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath());
        return vanilla.createAnvilRecipe(
                List.of(damaged),
                List.of(new ItemStack(repairMaterial)),
                List.of(repaired),
                uid
        );
    }
}
