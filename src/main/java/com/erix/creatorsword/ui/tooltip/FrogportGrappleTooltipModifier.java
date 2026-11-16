package com.erix.creatorsword.ui.tooltip;

import com.erix.creatorsword.config.CreatorSwordConfigs;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class FrogportGrappleTooltipModifier extends ItemDescription.Modifier {
    private final boolean aeronauticsLoaded = ModList.get().isLoaded("aeronautics");
    private int lastTongueLength = -1;

    public FrogportGrappleTooltipModifier(Item item) {
        super(item, Palette.STANDARD_CREATE);
    }

    @Override
    public void modify(ItemTooltipEvent context) {
        int tongueLength = CreatorSwordConfigs.server().frogportGrapple.tongueLength.get();
        if (checkLocale() || tongueLength != lastTongueLength) {
            String key = ItemDescription.getTooltipTranslationKey(item);
            ItemDescription.Builder builder = new ItemDescription.Builder(palette);
            builder.addSummary(I18n.get(key + ".summary"));
            builder.addBehaviour(I18n.get(key + ".condition1"),
                    I18n.get(key + ".behaviour1", tongueLength));
            builder.addBehaviour(I18n.get(key + ".condition2"), I18n.get(key + ".behaviour2"));
            if (aeronauticsLoaded) {
                builder.addBehaviour(I18n.get(key + ".aeronautics.condition"),
                        I18n.get(key + ".aeronautics.behaviour"));
            }
            description = builder.build();
            lastTongueLength = tongueLength;
        }
        if (description != null) {
            context.getToolTip().addAll(1, description.getCurrentLines());
        }
    }
}
