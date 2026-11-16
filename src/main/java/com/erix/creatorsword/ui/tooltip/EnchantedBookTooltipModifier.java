package com.erix.creatorsword.ui.tooltip;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class EnchantedBookTooltipModifier implements TooltipModifier {
    @Override
    public void modify(ItemTooltipEvent event) {
        ItemEnchantments enchantments = event.getItemStack()
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        boolean expanded = Screen.hasShiftDown();
        var tooltip = event.getToolTip();
        boolean hasDescription = false;
        for (var entry : enchantments.entrySet()) {
            var key = entry.getKey().unwrapKey().orElse(null);
            if (key == null || !CreatorSword.MODID.equals(key.location().getNamespace())) {
                continue;
            }
            String enchantmentKey = key.location().toLanguageKey("enchantment");
            String summaryKey = enchantmentKey + ".tooltip.summary";
            if (!I18n.exists(summaryKey)) {
                continue;
            }

            String name = Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString();
            for (int i = 0; i < tooltip.size(); i++) {
                if (!tooltip.get(i).getString().equals(name)) {
                    continue;
                }
                hasDescription = true;
                if (expanded) {
                    ItemDescription description = buildDescription(enchantmentKey);
                    var lines = description.linesOnShift();
                    tooltip.addAll(i + 1, lines.subList(2, lines.size()));
                }
                break;
            }
        }
        if (hasDescription) {
            tooltip.add(1, CreateLang.translateDirect("tooltip.holdForDescription",
                    CreateLang.translateDirect("tooltip.keyShift")
                            .withStyle(expanded ? ChatFormatting.WHITE : ChatFormatting.GRAY))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static ItemDescription buildDescription(String enchantmentKey) {
        String tooltipKey = enchantmentKey + ".tooltip";
        String summaryKey = tooltipKey + ".summary";
        ItemDescription.Builder builder = new ItemDescription.Builder(Palette.STANDARD_CREATE);
        for (String line : I18n.get(summaryKey).split("\\R", -1)) {
            builder.addSummary(line);
        }
        for (int index = 1; ; index++) {
            String conditionKey = tooltipKey + ".condition" + index;
            String behaviourKey = tooltipKey + ".behaviour" + index;
            if (!I18n.exists(conditionKey) || !I18n.exists(behaviourKey)) {
                break;
            }
            builder.addBehaviour(I18n.get(conditionKey), I18n.get(behaviourKey));
        }
        return builder.build();
    }

}
