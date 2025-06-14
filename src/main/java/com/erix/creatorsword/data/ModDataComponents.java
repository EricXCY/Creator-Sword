package com.erix.creatorsword.data;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import com.erix.creatorsword.CreatorSword;


public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreatorSword.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEAR_SHIELD_SPEED =
            DATA_COMPONENTS.registerComponentType("gear_shield_speed",
                    builder -> builder.persistent(com.mojang.serialization.Codec.FLOAT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GEAR_SHIELD_CHARGING =
            DATA_COMPONENTS.registerComponentType("gear_shield_charging",
                    builder -> builder.persistent(com.mojang.serialization.Codec.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> GEAR_SHIELD_CHARGE_START =
            DATA_COMPONENTS.registerComponentType("gear_shield_charge_start",
                    builder -> builder.persistent(com.mojang.serialization.Codec.LONG));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GEAR_SHIELD_DECAYING =
            DATA_COMPONENTS.registerComponentType("gear_shield_decaying",
                    builder -> builder.persistent(com.mojang.serialization.Codec.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> GEAR_SHIELD_LAST_DECAY =
            DATA_COMPONENTS.registerComponentType("gear_shield_last_decay",
                    builder -> builder.persistent(com.mojang.serialization.Codec.LONG));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEAR_SHIELD_ANGLE =
            DATA_COMPONENTS.registerComponentType("gear_shield_angle",
                    builder -> builder.persistent(com.mojang.serialization.Codec.FLOAT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> GEAR_SHIELD_LAST_SYNC =
            DATA_COMPONENTS.registerComponentType("gear_shield_last_sync",
                    builder -> builder.persistent(com.mojang.serialization.Codec.LONG));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, java.util.function.UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}


