package com.erix.creatorsword.datagen.tags;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import net.minecraft.data.tags.TagsProvider;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class CSItemTag extends ItemTagsProvider {
    public static final TagKey<Item> ENCHANTABLE_PNEUMATIC_BOOST =
            createTagKey("pneumatic_boost");
    public static final TagKey<Item> ENCHANTABLE_OVERDRIVE =
            createTagKey("overdrive");
    public static final TagKey<Item> ENCHANTABLE_STURDY =
            createTagKey("sturdy");
    public static final TagKey<Item> ENCHANTABLE_STICKY_TONGUE =
            createTagKey("sticky_tongue");

    private static TagKey<Item> createTagKey(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, name));
    }

    public CSItemTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                     CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreatorSword.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addAll(tag(ItemTags.SWORDS), CreatorSwordItems.CREATOR_SWORDS);
        addAll(tag(Tags.Items.MELEE_WEAPON_TOOLS), CreatorSwordItems.CREATOR_SWORDS);
        addAll(tag(Tags.Items.TOOLS_WRENCH), CreatorSwordItems.CREATOR_SWORDS);
        tag(Tags.Items.TOOLS)
                .add(FrogportGrappleItem.FROGPORT_GRAPPLE.get());

        tag(Tags.Items.TOOLS_SHIELD)
                .add(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        tag(ItemTags.VANISHING_ENCHANTABLE).add(
                CogwheelShieldItems.COGWHEEL_SHIELD.get(),
                FrogportGrappleItem.FROGPORT_GRAPPLE.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(
                CogwheelShieldItems.COGWHEEL_SHIELD.get(),
                FrogportGrappleItem.FROGPORT_GRAPPLE.get());

        tag(ENCHANTABLE_OVERDRIVE).add(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        tag(ENCHANTABLE_PNEUMATIC_BOOST).add(key(CogwheelShieldItems.COGWHEEL_SHIELD.get()));
        addAll(tag(ENCHANTABLE_PNEUMATIC_BOOST), CreatorSwordItems.CREATOR_SWORDS);
        tag(ENCHANTABLE_STURDY).addTag(ItemTags.DURABILITY_ENCHANTABLE);
        tag(ENCHANTABLE_STICKY_TONGUE).add(FrogportGrappleItem.FROGPORT_GRAPPLE.get());
    }

    private static void addAll(TagsProvider.TagAppender<Item> tag,
                               List<? extends Supplier<? extends Item>> items) {
        for (Supplier<? extends Item> supplier : items) {
            tag.add(key(supplier.get()));
        }
    }

    private static ResourceKey<Item> key(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item)
                .orElseThrow(() -> new IllegalStateException("Item is not registered: " + item));
    }
}
