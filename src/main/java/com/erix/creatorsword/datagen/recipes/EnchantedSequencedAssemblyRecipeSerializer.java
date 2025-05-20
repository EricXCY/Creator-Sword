package com.erix.creatorsword.datagen.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;


public class EnchantedSequencedAssemblyRecipeSerializer implements RecipeSerializer<EnchantedSequencedAssemblyRecipe> {

    private final MapCodec<EnchantedSequencedAssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(EnchantedSequencedAssemblyRecipe::getIngredient),
                    ProcessingOutput.CODEC.fieldOf("transitional_item").forGetter(r -> r.transitionalItem),
                    SequencedRecipe.CODEC.listOf().fieldOf("sequence").forGetter(EnchantedSequencedAssemblyRecipe::getSequence),
                    ProcessingOutput.CODEC.listOf().fieldOf("results").forGetter(r -> r.resultPool),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("loops").forGetter(r -> Optional.of(r.getLoops()))
            ).apply(i, (ingredient, transitionalItem, sequence, results, loops) -> {
                EnchantedSequencedAssemblyRecipe recipe = new EnchantedSequencedAssemblyRecipe(new SequencedAssemblyRecipeSerializer());
                recipe.ingredient = ingredient;
                recipe.transitionalItem = transitionalItem;
                recipe.sequence = new ArrayList<>(sequence);
                recipe.resultPool.addAll(results);
                recipe.loops = loops.orElse(5);

                for (int j = 0; j < recipe.sequence.size(); j++)
                    invokeInitMethod(recipe.sequence.get(j), recipe, j == 0);

                return recipe;
            })
    );

    public final StreamCodec<RegistryFriendlyByteBuf, EnchantedSequencedAssemblyRecipe> STREAM_CODEC = StreamCodec.of(
            this::toNetwork, this::fromNetwork
    );

    public EnchantedSequencedAssemblyRecipeSerializer() {}
    private void invokeInitMethod(SequencedRecipe<?> seq, EnchantedSequencedAssemblyRecipe parent, boolean isFirst) {
        try {
            Method method = SequencedRecipe.class.getDeclaredMethod("initFromSequencedAssembly", SequencedAssemblyRecipe.class, boolean.class);
            method.setAccessible(true);
            method.invoke(seq, parent, isFirst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void toNetwork(RegistryFriendlyByteBuf buffer, EnchantedSequencedAssemblyRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        SequencedRecipe.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getSequence());
        ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.resultPool);
        ProcessingOutput.STREAM_CODEC.encode(buffer, recipe.transitionalItem);
        buffer.writeInt(recipe.loops);
    }

    protected EnchantedSequencedAssemblyRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        EnchantedSequencedAssemblyRecipe recipe = new EnchantedSequencedAssemblyRecipe(new SequencedAssemblyRecipeSerializer());
        recipe.ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        recipe.sequence = new ArrayList<>(SequencedRecipe.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
        recipe.resultPool.addAll(ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
        recipe.transitionalItem = ProcessingOutput.STREAM_CODEC.decode(buffer);
        recipe.loops = buffer.readInt();
        return recipe;
    }

    @Override
    public @NotNull MapCodec<EnchantedSequencedAssemblyRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, EnchantedSequencedAssemblyRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
