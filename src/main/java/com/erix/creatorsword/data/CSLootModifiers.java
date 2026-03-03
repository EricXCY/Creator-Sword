package com.erix.creatorsword.data;

import com.erix.creatorsword.CreatorSword;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CSLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REG =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CreatorSword.MODID);

    public static final Supplier<MapCodec<ReplaceItemLootModifier>> REPLACE_ITEM =
            REG.register("replace_item", () -> ReplaceItemLootModifier.CODEC);

    public static void register(IEventBus bus) {
        REG.register(bus);
    }
}