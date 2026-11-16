package com.erix.creatorsword.fluid.ominous;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class OminousEssenceFluidType extends FluidType {
    private final int amplifier;

    public OminousEssenceFluidType(Properties properties, int amplifier) {
        super(properties);
        this.amplifier = Math.clamp(amplifier, 0, 4);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull FluidStack stack) {
        return switch (amplifier) {
            case 1 -> "fluid." + CreatorSword.MODID + ".ominous_essence_ii";
            case 2 -> "fluid." + CreatorSword.MODID + ".ominous_essence_iii";
            case 3 -> "fluid." + CreatorSword.MODID + ".ominous_essence_iv";
            case 4 -> "fluid." + CreatorSword.MODID + ".ominous_essence_v";
            default -> "fluid." + CreatorSword.MODID + ".ominous_essence";
        };
    }

    @Override
    public @NotNull Component getDescription(@NotNull FluidStack stack) {
        return Component.translatable(getDescriptionId(stack));
    }

}
