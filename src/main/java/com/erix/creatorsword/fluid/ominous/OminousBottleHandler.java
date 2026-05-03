package com.erix.creatorsword.fluid.ominous;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class OminousBottleHandler implements IFluidHandlerItem {
    private final ItemStack container;
    private boolean drained;

    public OminousBottleHandler(ItemStack container) {
        this.container = container;
        this.drained = false;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return drained ? new ItemStack(Items.GLASS_BOTTLE) : container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0 || drained)
            return FluidStack.EMPTY;

        return getContainedFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return OminousEssenceHelper.AMOUNT_PER_BOTTLE;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;

        FluidStack contained = getContainedFluid();

        if (!contained.is(resource.getFluid()))
            return FluidStack.EMPTY;

        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        if (drained)
            return FluidStack.EMPTY;

        if (maxDrain < OminousEssenceHelper.AMOUNT_PER_BOTTLE)
            return FluidStack.EMPTY;

        FluidStack result = getContainedFluid();

        if (action.execute())
            drained = true;

        return result;
    }

    private FluidStack getContainedFluid() {
        int amplifier = Math.clamp(
                container.getOrDefault(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 0),
                0,
                4
        );

        return OminousEssenceHelper.create(OminousEssenceHelper.AMOUNT_PER_BOTTLE, amplifier);
    }
}