package com.erix.creatorsword.datagen.Advancements;

import com.erix.creatorsword.item.cogwheel_shield.CogwheelshieldItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.erix.creatorsword.advancement.FullSpeedTrigger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    private static final class ModAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.display(
                    new ItemStack((ItemLike) CogwheelshieldItems.COGWHEEL_SHIELD),
                    Component.translatable("advancement.creatorsword.cogwheelshields_full_speed.title"),
                    Component.translatable("advancement.creatorsword.cogwheelshields_full_speed.description"),
                    null,
                    AdvancementType.CHALLENGE,
                    true,
                    true,
                    false
            );
            builder.addCriterion("get_full_speed", FullSpeedTrigger.criterion());
            builder.requirements(AdvancementRequirements.allOf(List.of("get_full_speed")));
            builder.save(saver, ResourceLocation.fromNamespaceAndPath("creatorsword", "achievements/cogwheelshields_full_speed"), existingFileHelper);
        }
    }
}