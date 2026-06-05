package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CogwheelShieldItems {
    public static final ResourceLocation COGWHEEL_SHIELD_HANDLE_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/handle");

    public static final ResourceLocation COGWHEEL_SHIELD_GEAR_MODEL =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/cogwheel_shield_handless");

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreatorSword.MODID);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CreatorSword.MODID);

    public static final DeferredItem<Item> COGWHEEL_SHIELD =
            ITEMS.registerItem("cogwheel_shield", CogwheelShieldItem::new);

    public static final DeferredHolder<EntityType<?>, EntityType<CogwheelShieldEntity>> COGWHEEL_SHIELD_ENTITY =
            ENTITY_TYPES.register(
                    "thrown_cogwheel_shield",
                    () -> EntityType.Builder.<CogwheelShieldEntity>of(CogwheelShieldEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(2)
                            .build("thrown_cogwheel_shield")
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ENTITY_TYPES.register(eventBus);
    }
}