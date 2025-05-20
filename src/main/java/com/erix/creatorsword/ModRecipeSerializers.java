package com.erix.creatorsword;

import com.erix.creatorsword.datagen.recipes.EnchantedSequencedAssemblyRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreatorSword.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> ENCHANTED_SEQUENCED_ASSEMBLY =
            RECIPE_SERIALIZERS.register("enchanted_sequenced_assembly",
                    EnchantedSequencedAssemblyRecipeSerializer::new);

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
