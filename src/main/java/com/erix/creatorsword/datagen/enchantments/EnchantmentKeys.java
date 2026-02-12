package com.erix.creatorsword.datagen.enchantments;

import com.erix.creatorsword.datagen.tags.ModItemTag;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;

import static com.erix.creatorsword.ResourceLocationUtil.getResourceLocation;


public class EnchantmentKeys {
    public static final ResourceKey<Enchantment> PNEUMATIC_BOOST = registerKey("pneumatic_boost");
    public static final ResourceKey<Enchantment> OVERDRIVE = registerKey("overdrive");
    public static final ResourceKey<Enchantment> STURDY = registerKey("sturdy");
    public static final ResourceKey<Enchantment> STICKY_TONGUE = registerKey("sticky_tongue");

    private static ResourceKey<Enchantment> registerKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, getResourceLocation(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<DamageType> damageTypes = context.lookup(Registries.DAMAGE_TYPE);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        register(context, PNEUMATIC_BOOST, new Enchantment.Builder(
                Enchantment.definition(
                        items.getOrThrow(ModItemTag.ENCHANTABLE_PNEUMATIC_BOOST),
                        7,
                        4,
                        Enchantment.dynamicCost(8, 8),
                        Enchantment.dynamicCost(30, 15),
                        1,
                        EquipmentSlotGroup.HAND
                )
        ));

        register(context, OVERDRIVE, new Enchantment.Builder(
                Enchantment.definition(
                        items.getOrThrow(ModItemTag.ENCHANTABLE_OVERDRIVE),
                        7,
                        1,
                        Enchantment.dynamicCost(8, 8),
                        Enchantment.dynamicCost(30, 15),
                        1,
                        EquipmentSlotGroup.HAND
                )
        ));

        register(context, STURDY, new Enchantment.Builder(
                Enchantment.definition(
                        items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                        8,
                        3,
                        Enchantment.dynamicCost(10, 10),
                        Enchantment.dynamicCost(30, 20),
                        1,
                        EquipmentSlotGroup.ANY
                )
        ));

        register(context, STICKY_TONGUE, new Enchantment.Builder(
                Enchantment.definition(
                        items.getOrThrow(ModItemTag.ENCHANTABLE_STICKY_TONGUE),
                        8,
                        3,
                        Enchantment.dynamicCost(8, 8),
                        Enchantment.dynamicCost(30, 15),
                        1,
                        EquipmentSlotGroup.HAND
                )
        ));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    public static int getEnchantmentLevel(RegistryAccess access, ResourceKey<Enchantment> enchantmentResourceKey, ItemStack handItem) {
        return EnchantmentHelper.getTagEnchantmentLevel(getEnchantmentHolder(access, enchantmentResourceKey), handItem);
    }

    public static Holder<Enchantment> getEnchantmentHolder(RegistryAccess access, ResourceKey<Enchantment> enchantmentResourceKey) {
        return access.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantmentResourceKey);
    }
}
