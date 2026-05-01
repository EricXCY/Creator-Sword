package com.erix.creatorsword.fluid.ominous;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class GlassBottleOminousFillingHandler implements IFluidHandlerItem {
    private final ItemStack container;
    private boolean filled;
    private int amplifier;

    public GlassBottleOminousFillingHandler(ItemStack container) {
        this.container = container;
        this.filled = false;
        this.amplifier = 0;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        if (!filled)
            return container;

        ItemStack result = new ItemStack(Items.OMINOUS_BOTTLE);
        result.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, Math.clamp(amplifier, 0, 4));
        return result;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return OminousEssenceHelper.AMOUNT_PER_BOTTLE;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == 0 && OminousEssenceHelper.isOminousEssence(stack);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (filled)
            return 0;

        if (!OminousEssenceHelper.isOminousEssence(resource))
            return 0;

        if (resource.getAmount() < OminousEssenceHelper.AMOUNT_PER_BOTTLE)
            return 0;

        int resourceAmplifier = OminousEssenceHelper.getAmplifier(resource);

        if (action.execute()) {
            filled = true;
            amplifier = resourceAmplifier;
        }

        return OminousEssenceHelper.AMOUNT_PER_BOTTLE;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        return FluidStack.EMPTY;
    }
}