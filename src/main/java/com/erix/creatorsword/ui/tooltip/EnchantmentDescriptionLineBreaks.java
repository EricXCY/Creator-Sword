package com.erix.creatorsword.ui.tooltip;

import com.erix.creatorsword.CreatorSword;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = CreatorSword.MODID, value = Dist.CLIENT)
public final class EnchantmentDescriptionLineBreaks {
    private static final List<String> DESCRIPTION_KEYS = List.of(
            "enchantment.creatorsword.pneumatic_boost.desc",
            "enchantment.creatorsword.overdrive.desc",
            "enchantment.creatorsword.inertial_storage.desc",
            "enchantment.creatorsword.sturdy.desc",
            "enchantment.creatorsword.sticky_tongue.desc");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        var elements = event.getTooltipElements();
        for (int i = 0; i < elements.size(); i++) {
            FormattedText text = elements.get(i).left().orElse(null);
            if (text == null) {
                continue;
            }
            String value = text.getString();
            if (!value.contains("\n") || DESCRIPTION_KEYS.stream()
                    .noneMatch(key -> I18n.exists(key) && value.contains(I18n.get(key)))) {
                continue;
            }

            List<FormattedText> lines = new ArrayList<>();
            List<FormattedText> parts = new ArrayList<>();
            text.visit((style, segment) -> {
                String[] fragments = segment.split("\n", -1);
                for (int j = 0; j < fragments.length; j++) {
                    if (j > 0) {
                        lines.add(FormattedText.composite(List.copyOf(parts)));
                        parts.clear();
                    }
                    parts.add(Component.literal(fragments[j]).withStyle(style));
                }
                return Optional.empty();
            }, Style.EMPTY);
            lines.add(FormattedText.composite(List.copyOf(parts)));
            elements.remove(i);
            for (FormattedText line : lines) {
                elements.add(i++, Either.left(line));
            }
            i--;
        }
    }
}
