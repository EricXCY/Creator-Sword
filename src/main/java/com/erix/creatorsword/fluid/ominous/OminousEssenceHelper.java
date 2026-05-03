package com.erix.creatorsword.fluid.ominous;

import com.erix.creatorsword.fluid.CSFluids;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class OminousEssenceHelper {
    public static final int AMOUNT_PER_BOTTLE = 250;

    public static FluidStack create(int amount, int amplifier) {
        int clamped = Math.clamp(amplifier, 0, 4);
        return new FluidStack(getSourceFluid(clamped), amount);
    }

    public static int getAmplifier(FluidStack stack) {
        if (stack.isEmpty())
            return 0;

        if (stack.is(CSFluids.OMINOUS_ESSENCE_II.get().getSource())
                || stack.is(CSFluids.OMINOUS_ESSENCE_II.get().getFlowing()))
            return 1;

        if (stack.is(CSFluids.OMINOUS_ESSENCE_III.get().getSource())
                || stack.is(CSFluids.OMINOUS_ESSENCE_III.get().getFlowing()))
            return 2;

        if (stack.is(CSFluids.OMINOUS_ESSENCE_IV.get().getSource())
                || stack.is(CSFluids.OMINOUS_ESSENCE_IV.get().getFlowing()))
            return 3;

        if (stack.is(CSFluids.OMINOUS_ESSENCE_V.get().getSource())
                || stack.is(CSFluids.OMINOUS_ESSENCE_V.get().getFlowing()))
            return 4;

        return 0;
    }

    public static boolean isOminousEssence(FluidStack stack) {
        if (stack.isEmpty())
            return false;

        return isOminousEssence(stack.getFluid());
    }

    public static boolean isOminousEssence(Fluid fluid) {
        return fluid == CSFluids.OMINOUS_ESSENCE.get().getSource()
                || fluid == CSFluids.OMINOUS_ESSENCE.get().getFlowing()
                || fluid == CSFluids.OMINOUS_ESSENCE_II.get().getSource()
                || fluid == CSFluids.OMINOUS_ESSENCE_II.get().getFlowing()
                || fluid == CSFluids.OMINOUS_ESSENCE_III.get().getSource()
                || fluid == CSFluids.OMINOUS_ESSENCE_III.get().getFlowing()
                || fluid == CSFluids.OMINOUS_ESSENCE_IV.get().getSource()
                || fluid == CSFluids.OMINOUS_ESSENCE_IV.get().getFlowing()
                || fluid == CSFluids.OMINOUS_ESSENCE_V.get().getSource()
                || fluid == CSFluids.OMINOUS_ESSENCE_V.get().getFlowing();
    }

    public static Fluid getSourceFluid() {
        return getSourceFluid(0);
    }

    public static Fluid getFlowingFluid() {
        return getFlowingFluid(0);
    }

    public static Fluid getSourceFluid(int amplifier) {
        return getFluidEntry(amplifier).get().getSource();
    }

    public static Fluid getFlowingFluid(int amplifier) {
        return getFluidEntry(amplifier).get().getFlowing();
    }

    private static FluidEntry<VirtualFluid> getFluidEntry(int amplifier) {
        return switch (Math.clamp(amplifier, 0, 4)) {
            case 1 -> CSFluids.OMINOUS_ESSENCE_II;
            case 2 -> CSFluids.OMINOUS_ESSENCE_III;
            case 3 -> CSFluids.OMINOUS_ESSENCE_IV;
            case 4 -> CSFluids.OMINOUS_ESSENCE_V;
            default -> CSFluids.OMINOUS_ESSENCE;
        };
    }
}