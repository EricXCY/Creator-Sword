package com.erix.creatorsword.datagen.advancements;

import com.erix.creatorsword.data.advancement.AdvancementSpeedTrigger;
import com.erix.creatorsword.data.advancement.TravelingFrogTrigger;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CSAdvancementProvider extends AdvancementProvider {
    public CSAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    private static final class ModAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> saver, @NotNull ExistingFileHelper existingFileHelper) {
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.display(
                    new ItemStack((ItemLike) CogwheelShieldItems.COGWHEEL_SHIELD),
                    Component.translatable("advancement.creatorsword.cogwheelshields_advancement_speed.title"),
                    Component.translatable("advancement.creatorsword.cogwheelshields_advancement_speed.description"),
                    null,
                    AdvancementType.CHALLENGE,
                    true,
                    true,
                    false
            );
            builder.addCriterion("reach_advancement_speed", AdvancementSpeedTrigger.criterion());
            builder.requirements(AdvancementRequirements.allOf(List.of("reach_advancement_speed")));
            builder.save(
                    saver,
                    ResourceLocation.fromNamespaceAndPath("creatorsword", "achievements/cogwheelshields_advancement_speed"),
                    existingFileHelper
            );

            builder = Advancement.Builder.advancement();
            builder.display(
                    new ItemStack((ItemLike) FrogportGrappleItem.FROGPORT_GRAPPLE),
                    Component.translatable("advancement.creatorsword.traveling_frog.title"),
                    Component.translatable("advancement.creatorsword.traveling_frog.description"),
                    null,
                    AdvancementType.CHALLENGE,
                    true,
                    true,
                    false
            );
            builder.addCriterion("reach_100000", TravelingFrogTrigger.criterion());
            builder.requirements(AdvancementRequirements.allOf(List.of("reach_100000")));
            builder.save(
                    saver,
                    ResourceLocation.fromNamespaceAndPath("creatorsword", "achievements/traveling_frog"),
                    existingFileHelper
            );
        }
    }
}