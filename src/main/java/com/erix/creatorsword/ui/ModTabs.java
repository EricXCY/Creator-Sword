package com.erix.creatorsword.ui;

import com.erix.creatorsword.item.cogwheel_shield.CogwheelshieldItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.erix.creatorsword.CreatorSword.MODID;
import static com.simibubi.create.AllCreativeModeTabs.BASE_CREATIVE_TAB;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATOR_SWORD =
            CREATIVE_TABS.register("creator_sword", ()-> CreativeModeTab.builder()
                    .withTabsBefore(BASE_CREATIVE_TAB.getKey())
                    .title(Component.translatable("itemGroup.creatorsword"))
                    .icon(()-> CogwheelshieldItems.COGWHEEL_SHIELD.get().getDefaultInstance())
                    .displayItems((Parameters, Output) -> {
                        Output.accept(CogwheelshieldItems.COGWHEEL_SHIELD.get());
                    }).build());
}
