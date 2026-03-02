package com.erix.creatorsword.item.frogport_grapple;

import com.erix.creatorsword.config.CreatorSwordConfigs;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import com.erix.creatorsword.config.FrogportGrappleCfg.RuleMode;

public final class GrappleTargetCondition {

    private GrappleTargetCondition() {}

    private enum GrappleTargetType {
        L0,
        L1,
        L2,
        L3
    }

    public static boolean canPullTarget(ItemStack stack, Entity target, Level level) {
        int sticky = getStickyTongueLevel(stack, level);

        var cfg = CreatorSwordConfigs.server().frogportGrapple;

        if (cfg == null || cfg.ruleMode.get() == RuleMode.DEFAULT) {
            GrappleTargetType type = classifyTarget(target);
            return switch (type) {
                case L0 -> sticky >= 0;
                case L1 -> sticky >= 1;
                case L2 -> sticky >= 2;
                case L3 -> sticky >= 3;
            };
        }

        if (target instanceof LivingEntity living) {
            var rules = cfg.customRules(level.registryAccess());
            return rules.canPull(living, sticky);
        }
        return false;
    }

    private static GrappleTargetType classifyTarget(Entity entity) {
        if (entity instanceof LivingEntity living) {

            if (isBoss(living))
                return GrappleTargetType.L3;

            MobCategory cat = living.getType().getCategory();

            if (cat == MobCategory.MONSTER)
                return GrappleTargetType.L2;

            if (cat == MobCategory.CREATURE
                    || cat == MobCategory.AMBIENT
                    || cat == MobCategory.WATER_CREATURE
                    || cat == MobCategory.WATER_AMBIENT
                    || cat == MobCategory.UNDERGROUND_WATER_CREATURE
                    || cat == MobCategory.AXOLOTLS) {
                return GrappleTargetType.L0;
            }
            return GrappleTargetType.L1;
        }
        return GrappleTargetType.L1;
    }

    private static boolean isBoss(LivingEntity entity) {
        return entity instanceof EnderDragon
                || entity instanceof WitherBoss
                || entity instanceof Warden;
    }

    private static int getStickyTongueLevel(ItemStack stack, Level level) {
        ItemEnchantments ench = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var holder = reg.get(EnchantmentKeys.STICKY_TONGUE).orElse(null);
        if (holder == null)
            return 0;

        return ench.getLevel(holder);
    }
}