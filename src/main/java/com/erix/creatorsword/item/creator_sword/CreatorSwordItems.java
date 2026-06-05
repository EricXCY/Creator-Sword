package com.erix.creatorsword.item.creator_sword;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class CreatorSwordItems {
    public static final ResourceLocation CREATOR_SWORD_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/sword");

    public static final ResourceLocation CREATOR_SWORD_GEAR_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/gear");

    public static final ResourceLocation NETHERITE_CREATOR_SWORD_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/netherite_sword");

    public static final ResourceLocation NETHERITE_CREATOR_SWORD_GEAR_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/gear");

    public static final ResourceLocation CNY_CREATOR_SWORD_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/chinese_new_year_netherite_sword");

    public static final ResourceLocation CNY_CREATOR_SWORD_GEAR_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/chinese_new_year_gear");

    public static final ResourceLocation TRIAL_CREATOR_SWORD_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/trial_sword");

    public static final ResourceLocation TRIAL_CREATOR_SWORD_GEAR_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/creator_sword/trial_gear");

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredItem<SwordItem> CREATOR_SWORD = ITEMS.registerItem("creator_sword",
            CreatorSwordItem::new);

    public static final DeferredItem<SwordItem> NETHERITE_CREATOR_SWORD = ITEMS.registerItem("netherite_creator_sword",
            NetheriteCreatorSwordItem::new);

    public static final DeferredItem<SwordItem> CNY_CREATOR_SWORD = ITEMS.registerItem("cny_creator_sword",
            CNYCreatorSwordItem::new);

    public static final DeferredItem<SwordItem> TRIAL_CREATOR_SWORD = ITEMS.registerItem("trial_creator_sword",
            TrialCreatorSwordItem::new);

    public static final List<Supplier<? extends Item>> CREATOR_SWORDS = List.of(
            CREATOR_SWORD,
            NETHERITE_CREATOR_SWORD,
            CNY_CREATOR_SWORD,
            TRIAL_CREATOR_SWORD
    );
}