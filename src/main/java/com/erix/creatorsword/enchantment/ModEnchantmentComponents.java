package com.erix.creatorsword.enchantment;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEnchantmentComponents {
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, "creatorsword");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PneumaticBoostEffect>> PNEUMATIC_BOOST =
            ENCHANTMENT_COMPONENT_TYPES.register("pneumatic_boost",
                    () -> DataComponentType.<PneumaticBoostEffect>builder()
                            .persistent(PneumaticBoostEffect.CODEC)
                            .build());
}
