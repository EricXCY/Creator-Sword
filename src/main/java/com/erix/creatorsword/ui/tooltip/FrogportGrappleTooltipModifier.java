package com.erix.creatorsword.ui.tooltip;

import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class FrogportGrappleTooltipModifier extends ItemDescription.Modifier {
    private final boolean aeronauticsLoaded = ModList.get().isLoaded("aeronautics");

    public FrogportGrappleTooltipModifier(Item item) {
        super(item, Palette.STANDARD_CREATE);
    }

    @Override
    public void modify(ItemTooltipEvent context) {
        if (checkLocale()) {
            String key = ItemDescription.getTooltipTranslationKey(item);
            ItemDescription.Builder builder = new ItemDescription.Builder(palette);
            ItemDescription.fillBuilder(builder, key);
            if (aeronauticsLoaded) {
                builder.addBehaviour(I18n.get(key + ".aeronautics.condition"),
                        I18n.get(key + ".aeronautics.behaviour"));
            }
            description = builder.build();
        }
        if (description != null) {
            context.getToolTip().addAll(1, description.getCurrentLines());
        }
    }
}
