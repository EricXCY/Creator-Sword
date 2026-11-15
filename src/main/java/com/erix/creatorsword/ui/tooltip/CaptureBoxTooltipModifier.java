package com.erix.creatorsword.ui.tooltip;

import com.erix.creatorsword.item.capture_box.CaptureBoxLoot;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class CaptureBoxTooltipModifier extends ItemDescription.Modifier {
    private int lastRolls = -1;

    public CaptureBoxTooltipModifier(Item item) {
        super(item, Palette.STANDARD_CREATE);
    }

    @Override
    public void modify(ItemTooltipEvent context) {
        int rolls = CaptureBoxLoot.getLootRolls();
        if (checkLocale() || rolls != lastRolls) {
            String key = ItemDescription.getTooltipTranslationKey(item);
            ItemDescription.Builder builder = new ItemDescription.Builder(palette);
            builder.addSummary(I18n.get(key + ".summary"));
            builder.addBehaviour(I18n.get(key + ".condition1"), I18n.get(key + ".behaviour1"));
            builder.addBehaviour(I18n.get(key + ".condition2"), I18n.get(key + ".behaviour2", rolls, rolls));
            description = builder.build();
            lastRolls = rolls;
        }
        if (description != null) {
            context.getToolTip().addAll(1, description.getCurrentLines());
        }
    }
}
