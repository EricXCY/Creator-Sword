package com.erix.creatorsword.compat.jei;

import com.erix.creatorsword.fluid.ominous.OminousEssenceHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OminousEssenceSubtypeInterpreter implements ISubtypeInterpreter<FluidStack> {
    @Override
    public @Nullable Object getSubtypeData(@NotNull FluidStack ingredient, @NotNull UidContext context) {
        if (!OminousEssenceHelper.isOminousEssence(ingredient))
            return null;

        return "ominous_essence;amplifier=" + OminousEssenceHelper.getAmplifier(ingredient);
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(@NotNull FluidStack ingredient, @NotNull UidContext context) {
        return "";
    }
}