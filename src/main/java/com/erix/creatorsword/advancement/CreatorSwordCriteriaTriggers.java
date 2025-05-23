package com.erix.creatorsword.advancement;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class CreatorSwordCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, CreatorSword.MODID);
    public static final DeferredHolder<CriterionTrigger<?>, FullSpeedTrigger> FULL_SPEED =
            TRIGGERS.register("cogwheelshields_full_speed", FullSpeedTrigger::new);

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, PlayerTrigger> register(String pName) {
        return register(pName, new PlayerTrigger());
    }

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String pName, T pTrigger) {
        return TRIGGERS.register(pName, () -> pTrigger);
    }

    public static void init() {}

    public static Criterion<?> createCriterion(DeferredHolder<CriterionTrigger<?>, PlayerTrigger> holder) {
        return holder.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}