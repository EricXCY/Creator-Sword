package com.erix.creatorsword.fluid.ominous;

import com.erix.creatorsword.fluid.CSFluids;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class OminousEssenceHelper {
    public static final int AMOUNT_PER_BOTTLE = 250;

    public static FluidStack create(int amount, int amplifier) {
        int clamped = Math.clamp(amplifier, 0, 4);

        FluidStack stack = new FluidStack(getSourceFluid(), amount);
        stack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, clamped);

        return stack;
    }

    public static int getAmplifier(FluidStack stack) {
        if (!isOminousEssence(stack))
            return 0;

        return Math.clamp(
                stack.getOrDefault(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 0),
                0,
                4
        );
    }

    public static boolean isOminousEssence(FluidStack stack) {
        if (stack.isEmpty())
            return false;

        return stack.is(getSourceFluid())
                || stack.is(getFlowingFluid());
    }

    public static Fluid getSourceFluid() {
        return CSFluids.OMINOUS_ESSENCE.get().getSource();
    }

    public static Fluid getFlowingFluid() {
        return CSFluids.OMINOUS_ESSENCE.get().getFlowing();
    }
}