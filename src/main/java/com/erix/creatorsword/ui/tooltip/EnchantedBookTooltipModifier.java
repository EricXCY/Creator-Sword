package com.erix.creatorsword.ui.tooltip;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.foundation.item.TooltipHelper;
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
            String descriptionKey = key.location().toLanguageKey("enchantment") + ".desc";
            if (!I18n.exists(descriptionKey)) {
                continue;
            }

            String name = Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString();
            // Reuse the vanilla name line instead of adding a second enchantment heading.
            for (int i = 0; i < tooltip.size(); i++) {
                if (!tooltip.get(i).getString().equals(name)) {
                    continue;
                }
                hasDescription = true;
                if (expanded) {
                    tooltip.addAll(i + 1, TooltipHelper.cutStringTextComponent(
                            I18n.get(descriptionKey), Palette.STANDARD_CREATE.primary(),
                            Palette.STANDARD_CREATE.highlight(), 1));
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
}
