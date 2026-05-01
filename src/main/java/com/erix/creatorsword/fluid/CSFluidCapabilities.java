package com.erix.creatorsword.fluid;

import com.erix.creatorsword.fluid.ominous.GlassBottleOminousFillingHandler;
import com.erix.creatorsword.fluid.ominous.OminousBottleHandler;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CSFluidCapabilities {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CSFluidCapabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) -> new OminousBottleHandler(stack),
                Items.OMINOUS_BOTTLE
        );

        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) -> new GlassBottleOminousFillingHandler(stack),
                Items.GLASS_BOTTLE
        );
    }
}