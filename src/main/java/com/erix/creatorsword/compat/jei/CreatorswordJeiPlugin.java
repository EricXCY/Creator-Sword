package com.erix.creatorsword.compat.jei;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@JeiPlugin
public class CreatorswordJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = CreatorSword.asResource("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // registration.addRecipeCategories(new XxxCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // ===== Anvil repair =====
        IVanillaRecipeFactory vanilla = registration.getVanillaRecipeFactory();
        if (Minecraft.getInstance().level == null) return;

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CogwheelShieldItems.COGWHEEL_SHIELD.get()),
                        new ItemStack(com.simibubi.create.AllBlocks.SHAFT.asItem()),
                        new ItemStack(CogwheelShieldItems.COGWHEEL_SHIELD.get())
                ))
        );

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CreatorSwordItems.CREATOR_SWORD.get()),
                        new ItemStack(com.simibubi.create.AllItems.BRASS_SHEET.asItem()),
                        new ItemStack(CreatorSwordItems.CREATOR_SWORD.get())
                ))
        );

        registration.addRecipes(
                RecipeTypes.ANVIL,
                List.of(makeAnvilRepair(vanilla,
                        new ItemStack(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get()),
                        new ItemStack(net.minecraft.world.item.Items.NETHERITE_INGOT),
                        new ItemStack(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get())
                ))
        );
    }

    private static IJeiAnvilRecipe makeAnvilRepair(
            IVanillaRecipeFactory vanilla,
            ItemStack input,
            ItemStack repairMat,
            ItemStack output
    ) {
        ItemStack damaged = input.copy();
        damaged.setDamageValue(damaged.getMaxDamage());

        int repair = (int) Math.floor(damaged.getMaxDamage() * 0.25) * Math.max(1, repairMat.getCount());
        ItemStack repaired = output.copy();
        repaired.setDamageValue(Math.max(0, damaged.getDamageValue() - repair));

        return vanilla.createAnvilRecipe(
                List.of(damaged),
                List.of(repairMat),
                List.of(repaired)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // registration.addRecipeCatalyst(new ItemStack(ModItems.YOUR_BLOCK.get()), XxxRecipeType.TYPE);
    }
}
