package com.erix.creatorsword.ui.tooltip;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

public class TemplateTooltipModifier extends ItemDescription.Modifier {
    private int generation = -1;

    public TemplateTooltipModifier(Item item) {
        super(item, Palette.STANDARD_CREATE);
    }

    @Override
    public void modify(ItemTooltipEvent context) {
        boolean changed = checkLocale();
        if (changed || generation != TextureTooltipColors.generation) {
            generation = TextureTooltipColors.generation;
            ItemDescription.Builder builder = new SummaryBuilder(TextureTooltipColors.get(item));
            ItemDescription.fillBuilder(builder, ItemDescription.getTooltipTranslationKey(item));
            description = builder.build();
        }
        context.getToolTip().addAll(1, description.getCurrentLines());
    }

    private static class SummaryBuilder extends ItemDescription.Builder {
        private final Palette summaryPalette;

        private SummaryBuilder(Palette summaryPalette) {
            super(Palette.STANDARD_CREATE);
            this.summaryPalette = summaryPalette;
        }

        @Override
        public ItemDescription build() {
            ItemDescription standard = super.build();
            if (summary.isEmpty()) {
                return standard;
            }
            List<Component> coloredSummary = new ArrayList<>();
            int originalLineCount = 0;
            for (String paragraph : summary) {
                originalLineCount += TooltipHelper.cutStringTextComponent(paragraph, palette).size();
                coloredSummary.addAll(TooltipHelper.cutStringTextComponent(paragraph, summaryPalette));
            }
            int summaryStart = actions.isEmpty() ? 2 : 3;
            List<Component> shiftLines = new ArrayList<>(standard.linesOnShift());
            shiftLines.subList(summaryStart, summaryStart + originalLineCount).clear();
            shiftLines.addAll(summaryStart, coloredSummary);
            return new ItemDescription(standard.lines(), ImmutableList.copyOf(shiftLines),
                    standard.linesOnCtrl());
        }
    }
}
