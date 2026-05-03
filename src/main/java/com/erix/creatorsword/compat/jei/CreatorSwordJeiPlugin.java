package com.erix.creatorsword.compat.jei;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.fluid.ominous.OminousEssenceHelper;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
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
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        // registration.addRecipeCategories(new XxxCategory(registration.getJeiHelpers().getGuiHelper()));
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
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory vanilla = registration.getVanillaRecipeFactory();
        if (Minecraft.getInstance().level == null) return;

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CogwheelShieldItems.COGWHEEL_SHIELD.get()),
                        new ItemStack(com.simibubi.create.AllBlocks.SHAFT.asItem()),
                        new ItemStack(CogwheelShieldItems.COGWHEEL_SHIELD.get()),
                        CreatorSword.asResource("anvil_repair/cogwheel_shield")
                ))
        );

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CreatorSwordItems.CREATOR_SWORD.get()),
                        new ItemStack(com.simibubi.create.AllItems.BRASS_SHEET.asItem()),
                        new ItemStack(CreatorSwordItems.CREATOR_SWORD.get()),
                        CreatorSword.asResource("anvil_repair/creator_sword")
                ))
        );

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get()),
                        new ItemStack(net.minecraft.world.item.Items.NETHERITE_INGOT),
                        new ItemStack(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get()),
                        CreatorSword.asResource("anvil_repair/netherite_creator_sword")
                ))
        );

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CreatorSwordItems.CNY_CREATOR_SWORD.get()),
                        new ItemStack(net.minecraft.world.item.Items.NETHERITE_INGOT),
                        new ItemStack(CreatorSwordItems.CNY_CREATOR_SWORD.get()),
                        CreatorSword.asResource("anvil_repair/cny_creator_sword")
                ))
        );

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CreatorSwordItems.TRIAL_CREATOR_SWORD.get()),
                        new ItemStack(net.minecraft.world.item.Items.NETHERITE_INGOT),
                        new ItemStack(CreatorSwordItems.TRIAL_CREATOR_SWORD.get()),
                        CreatorSword.asResource("anvil_repair/trial_creator_sword")
                ))
        );
    }

    private static IJeiAnvilRecipe makeAnvilRepair(
            IVanillaRecipeFactory vanilla,
            ItemStack input,
            ItemStack repairMat,
            ItemStack output,
            ResourceLocation uid
    ) {
        ItemStack damaged = input.copy();
        damaged.setDamageValue(damaged.getMaxDamage());

        int repair = (int) Math.floor(damaged.getMaxDamage() * 0.25) * Math.max(1, repairMat.getCount());
        ItemStack repaired = output.copy();
        repaired.setDamageValue(Math.max(0, damaged.getDamageValue() - repair));

        return vanilla.createAnvilRecipe(
                List.of(damaged),
                List.of(repairMat),
                List.of(repaired),
                uid
        );
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        // registration.addRecipeCatalyst(new ItemStack(ModItems.YOUR_BLOCK.get()), XxxRecipeType.TYPE);
    }
}
