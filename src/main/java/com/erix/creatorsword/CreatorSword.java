package com.erix.creatorsword;

import com.erix.creatorsword.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.data.ModDataComponents;
import com.erix.creatorsword.enchantment.ModEnchantmentComponents;
import com.erix.creatorsword.entity.ModEntities;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.incomplete_creator_sword.IncompleteItems;
import com.erix.creatorsword.network.NetworkHandler;
import com.erix.creatorsword.ui.ModTabs;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CreatorSword.MODID)
public class CreatorSword
{
    public static final String MODID = "creatorsword";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreatorSword(IEventBus modEventBus, ModContainer modContainer) {
        CreatorSwordItems.ITEMS.register(modEventBus);
        CogwheelShieldItems.ITEMS.register(modEventBus);
        IncompleteItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        modEventBus.register(NetworkHandler.class);
        CreatorSwordCriteriaTriggers.TRIGGERS.register(modEventBus);
        ModEntities.register(modEventBus);
        ModEnchantmentComponents.ENCHANTMENT_COMPONENT_TYPES.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            com.erix.creatorsword.client.ClientSetup.init(modEventBus);
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

