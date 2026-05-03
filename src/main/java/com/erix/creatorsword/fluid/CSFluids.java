package com.erix.creatorsword.fluid;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.fluid.ominous.OminousEssenceFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;

public class CSFluids {
    private static final CreateRegistrate REGISTRATE = CreatorSword.registrate();

    public static final FluidEntry<VirtualFluid> OMINOUS_ESSENCE =
            ominousEssence("ominous_essence", 0);

    public static final FluidEntry<VirtualFluid> OMINOUS_ESSENCE_II =
            ominousEssence("ominous_essence_ii", 1);

    public static final FluidEntry<VirtualFluid> OMINOUS_ESSENCE_III =
            ominousEssence("ominous_essence_iii", 2);

    public static final FluidEntry<VirtualFluid> OMINOUS_ESSENCE_IV =
            ominousEssence("ominous_essence_iv", 3);

    public static final FluidEntry<VirtualFluid> OMINOUS_ESSENCE_V =
            ominousEssence("ominous_essence_v", 4);

    private static FluidEntry<VirtualFluid> ominousEssence(String name, int amplifier) {
        return REGISTRATE.virtualFluid(
                        name,
                        (properties, stillTexture, flowingTexture) ->
                                new OminousEssenceFluidType(properties, stillTexture, flowingTexture, amplifier),
                        VirtualFluid::createSource,
                        VirtualFluid::createFlowing
                )
                .register();
    }

    public static void register() {
        // Load this class
    }
}