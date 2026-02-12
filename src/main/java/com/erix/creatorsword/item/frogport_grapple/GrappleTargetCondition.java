package com.erix.creatorsword.item.frogport_grapple;

import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public final class GrappleTargetCondition {

    private GrappleTargetCondition() {}

    private enum GrappleTargetType {
        FRIENDLY,
        NEUTRAL,
        HOSTILE,
        IMMUNE
    }

    public static boolean canPullTarget(ItemStack stack, Entity target, Level level) {
        GrappleTargetType type = classifyTarget(target);
        int sricky = getStickyTongueLevel(stack, level);

        return switch (type) {
            case FRIENDLY -> sricky >= 0;
            case NEUTRAL  -> sricky >= 1;
            case HOSTILE  -> sricky >= 2;
            case IMMUNE   -> sricky >= 3;
        };
    }

    private static GrappleTargetType classifyTarget(Entity entity) {
        if (entity instanceof LivingEntity living) {
            // Boss
            if (isBoss(living))
                return GrappleTargetType.IMMUNE;

            MobCategory cat = living.getType().getCategory();

            if (cat == MobCategory.MONSTER)
                return GrappleTargetType.HOSTILE;

            // 其他生物 - 友好
            if (cat == MobCategory.CREATURE
                    || cat == MobCategory.AMBIENT
                    || cat == MobCategory.WATER_CREATURE
                    || cat == MobCategory.WATER_AMBIENT
                    || cat == MobCategory.UNDERGROUND_WATER_CREATURE
                    || cat == MobCategory.AXOLOTLS) {
                return GrappleTargetType.FRIENDLY;
            }

            // 剩下的当中立
            return GrappleTargetType.NEUTRAL;
        }

        // 其他非生物实体默认中立
        return GrappleTargetType.NEUTRAL;
    }

    private static boolean isBoss(LivingEntity entity) {
        return entity instanceof EnderDragon
                || entity instanceof WitherBoss
                || entity instanceof Warden;
    }

    private static int getStickyTongueLevel(ItemStack stack, Level level) {
        ItemEnchantments ench = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        var reg = level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        var holder = reg.get(EnchantmentKeys.STICKY_TONGUE).orElse(null);
        if (holder == null)
            return 0;

        return ench.getLevel(holder);
    }
}
