package com.erix.creatorsword.item.frogport_grapple;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FrogportGrappleTravelStat {

    public static final ResourceLocation ID = CreatorSword.asResource("frogport_grapple_travel");

    public static final DeferredRegister<ResourceLocation> CS_STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, CreatorSword.MODID);

    public static final DeferredHolder<ResourceLocation, ResourceLocation> FROGPORT_GRAPPLE_TRAVEL =
            CS_STATS.register("frogport_grapple_travel", () -> ID);

    public static void register(IEventBus modBus) {
        CS_STATS.register(modBus);
    }
}
