package com.erix.creatorsword.advancement;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreatorSwordCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, CreatorSword.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, FullSpeedTrigger> FULL_SPEED =
            TRIGGERS.register("cogwheelshields_full_speed", FullSpeedTrigger::new);

    public static void register(IEventBus modBus) {
        TRIGGERS.register(modBus);
    }
}