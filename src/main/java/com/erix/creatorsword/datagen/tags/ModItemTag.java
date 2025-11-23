package com.erix.creatorsword.datagen.tags;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTag extends ItemTagsProvider {
    public static final TagKey<Item> ENCHANTABLE_PNEUMATIC_BOOST =
            createTagKey("pneumatic_boost");
    public static final TagKey<Item> ENCHANTABLE_OVERDRIVE =
            createTagKey("overdrive");

    private static TagKey<Item> createTagKey(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, name));
    }

    public ModItemTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                  CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreatorSword.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ItemTags.SWORDS)
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());
        tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());
        tag(Tags.Items.TOOLS_WRENCH)
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());

        tag(Tags.Items.TOOLS_SHIELD)
                .add(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        tag(ItemTags.BOW_ENCHANTABLE)
                .add(FrogportGrappleItem.FROGPORT_GRAPPLE.get());

        tag(ENCHANTABLE_OVERDRIVE).add(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        tag(ENCHANTABLE_PNEUMATIC_BOOST)
                .add(CogwheelShieldItems.COGWHEEL_SHIELD.get())
                .add(CreatorSwordItems.CREATOR_SWORD.get())
                .add(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());
    }
}
