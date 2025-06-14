package com.erix.creatorsword.entity;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, CreatorSword.MODID);

    public static final DeferredHolder<EntityType<?>,EntityType<ThrownCogwheelShield>> COGWHEEL_SHIELD_ENTITY = ENTITY_TYPES.register("thrown_cogwheel_shield",
            ()-> EntityType.Builder.<ThrownCogwheelShield>of(ThrownCogwheelShield::new, MobCategory.MISC).sized(0.5f,0.5f).build("thrown_cogwheel_shield"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
