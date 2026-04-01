package com.erix.creatorsword.item.smithing_template;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class CSmithingTemplateItem {

    private static final List<ResourceLocation> SWORD_BASE_SLOT_TEXTURES = List.of(
            ResourceLocation.withDefaultNamespace("item/empty_slot_sword")
    );

    private static final List<ResourceLocation> INGOT_ADDITION_SLOT_TEXTURES = List.of(
            ResourceLocation.withDefaultNamespace("item/empty_slot_ingot")
    );

    private CSmithingTemplateItem() {
    }

    public static Item createCrimsonAfterglowTemplate() {
        return createTemplate(
                "crimson_afterglow_smithing_template",
                SWORD_BASE_SLOT_TEXTURES,
                INGOT_ADDITION_SLOT_TEXTURES
        );
    }

    private static Item createTemplate(
            String name,
            List<ResourceLocation> baseSlotTextures,
            List<ResourceLocation> additionSlotTextures
    ) {
        String key = "item." + CreatorSword.MODID + "." + name;

        return new SmithingTemplateItem(
                Component.translatable(key + ".applies_to").withStyle(ChatFormatting.BLUE),
                Component.translatable(key + ".ingredients").withStyle(ChatFormatting.BLUE),
                Component.translatable(key + ".upgrade_description").withStyle(ChatFormatting.GRAY),
                Component.translatable(key + ".base_slot_description"),
                Component.translatable(key + ".additions_slot_description"),
                baseSlotTextures,
                additionSlotTextures
        );
    }
}