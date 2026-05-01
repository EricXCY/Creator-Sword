package com.erix.creatorsword.fluid;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.fluid.ominous.OminousEssenceFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;

public class CSFluids {
    private static final CreateRegistrate REGISTRATE = CreatorSword.registrate();

    public static final FluidEntry<VirtualFluid> OMINOUS_ESSENCE =
            REGISTRATE.virtualFluid(
                            "ominous_essence",
                            OminousEssenceFluidType::new,
                            VirtualFluid::createSource,
                            VirtualFluid::createFlowing
                    )
                    .register();

    public static void register() {
        // Load this class
    }
}