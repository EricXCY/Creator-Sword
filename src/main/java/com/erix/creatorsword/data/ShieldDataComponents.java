package com.erix.creatorsword.data;

import com.erix.creatorsword.CreatorSword;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ShieldDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreatorSword.MODID);

    private static final StreamCodec<ByteBuf, Float> SC_FLOAT = ByteBufCodecs.FLOAT;
    private static final StreamCodec<ByteBuf, Boolean> SC_BOOL = ByteBufCodecs.BOOL;
    private static final StreamCodec<ByteBuf, Long> SC_LONG = ByteBufCodecs.VAR_LONG;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEAR_SHIELD_SPEED =
            DATA_COMPONENTS.registerComponentType("gear_shield_speed",
                    b -> b.persistent(Codec.FLOAT).networkSynchronized(SC_FLOAT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GEAR_SHIELD_CHARGING =
            DATA_COMPONENTS.registerComponentType("gear_shield_charging",
                    b -> b.persistent(Codec.BOOL).networkSynchronized(SC_BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GEAR_SHIELD_DECAYING =
            DATA_COMPONENTS.registerComponentType("gear_shield_decaying",
                    b -> b.persistent(Codec.BOOL).networkSynchronized(SC_BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEAR_SHIELD_ANGLE =
            DATA_COMPONENTS.registerComponentType("gear_shield_angle",
                    b -> b.persistent(Codec.FLOAT).networkSynchronized(SC_FLOAT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> GEAR_SHIELD_LAST_UPDATE =
            DATA_COMPONENTS.registerComponentType("gear_shield_last_update",
                    b -> b.persistent(Codec.LONG).networkSynchronized(SC_LONG));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> GEAR_SHIELD_LAST_AIR_TICK =
            DATA_COMPONENTS.registerComponentType("gear_shield_last_air_tick",
                    b -> b.persistent(Codec.LONG).networkSynchronized(SC_LONG));
}
