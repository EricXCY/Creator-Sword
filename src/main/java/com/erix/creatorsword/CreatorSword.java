package com.erix.creatorsword;

import com.erix.creatorsword.item.cogwheel_shield.CogwheelshieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.ui.ModTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreatorSword.MODID)
public class CreatorSword
{
    public static final String MODID = "creatorsword";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreatorSword(IEventBus modEventBus, ModContainer modContainer)
    {
        CogwheelshieldItems.ITEMS.register(modEventBus);
        CreatorSwordItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_TABS.register(modEventBus);
        modEventBus.register(KeyBindings.class);
    }
}

