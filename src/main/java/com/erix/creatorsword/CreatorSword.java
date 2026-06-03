package com.erix.creatorsword;

import com.erix.creatorsword.item.cogwheel_shield.logic.CogwheelShieldClientEvents;
import com.erix.creatorsword.compat.aeronautics.FrogportAeronauticsCompat;
import com.erix.creatorsword.compat.ftbultimine.CreatorSwordFTBUltimineCompat;
import com.erix.creatorsword.config.CreatorSwordConfigs;
import com.erix.creatorsword.data.CSConditions;
import com.erix.creatorsword.data.CSDataComponents;
import com.erix.creatorsword.data.CSLootModifiers;
import com.erix.creatorsword.data.advancement.CreatorSwordCriteriaTriggers;
import com.erix.creatorsword.enchantment.CSEnchantmentComponents;
import com.erix.creatorsword.fluid.CSFluidCapabilities;
import com.erix.creatorsword.fluid.CSFluids;
import com.erix.creatorsword.item.CSItems;
import com.erix.creatorsword.item.capture_box.CaptureBoxItem;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldClientSetup;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.cogwheel_shield.logic.CogwheelShieldNetwork;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleTravelStat;
import com.erix.creatorsword.item.incomplete_creator_sword.IncompleteCreatorSwordItems;
import com.erix.creatorsword.item.incomplete_enchantment_book.IncompleteEnchantmentBookItems;
import com.erix.creatorsword.item.smithing_template.SmithingTemplateItems;
import com.erix.creatorsword.item.supreme_glue.SupremeGlueItem;
import com.erix.creatorsword.ui.ModTabs;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(CreatorSword.MODID)
public class CreatorSword {
    public static final String MODID = "creatorsword";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public CreatorSword(IEventBus modEventBus, ModContainer container) {
        REGISTRATE.registerEventListeners(modEventBus);

        CreatorSwordConfigs.register(container);

        CreatorSwordItems.ITEMS.register(modEventBus);
        CogwheelShieldItems.register(modEventBus);
        IncompleteCreatorSwordItems.ITEMS.register(modEventBus);
        IncompleteEnchantmentBookItems.ITEMS.register(modEventBus);
        SupremeGlueItem.ITEMS.register(modEventBus);
        FrogportGrappleItem.ITEMS.register(modEventBus);
        FrogportGrappleTravelStat.register(modEventBus);
        CaptureBoxItem.ITEMS.register(modEventBus);
        CSItems.register(modEventBus);
        SmithingTemplateItems.register(modEventBus);

        ModTabs.CREATIVE_TABS.register(modEventBus);
        CSDataComponents.DATA_COMPONENTS.register(modEventBus);
        CreatorSwordCriteriaTriggers.TRIGGERS.register(modEventBus);

        CSEnchantmentComponents.ENCHANTMENT_COMPONENT_TYPES.register(modEventBus);
        CSLootModifiers.register(modEventBus);
        CSConditions.register(modEventBus);

        CSFluids.register();
        CSFluidCapabilities.register(modEventBus);

        modEventBus.register(CogwheelShieldNetwork.class);
        modEventBus.addListener(CSDataGenerator::register);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            KeyBindings.register(modEventBus);
            CogwheelShieldClientSetup.register(modEventBus);
            CogwheelShieldClientEvents.register();
        }

        if (ModList.get().isLoaded("ftbultimine")) {
            CreatorSwordFTBUltimineCompat.init();
        }

        if (ModList.get().isLoaded("aeronautics") || ModList.get().isLoaded("sable")) {
            FrogportAeronauticsCompat.init();
        }
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}