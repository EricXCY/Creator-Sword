package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = CreatorSword.MODID)
public class CogwheelShieldEvents {

    public static float currentSpeed = 0f;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isBlockingWithCogwheelShield(player)) return;
        if (currentSpeed < 64f) return;

        DamageSource src = event.getSource();
        if (src.getEntity() instanceof LivingEntity attacker) {
            // 64转速=4点伤害，每翻倍+1
            float bonus = 4f + (float)(Math.log(currentSpeed / 64f) / Math.log(2));
            attacker.hurt(player.damageSources().thorns(player), bonus);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = player.getUseItem();
        if (stack.getItem() != CogwheelshieldItems.COGWHEEL_SHIELD.get()) return;
        if (currentSpeed < 64f) return;

        DamageSource src = event.getDamageSource();
        if (src.getDirectEntity() instanceof Projectile proj) {
            // 64转速 = 1.0 倍速，每翻倍额外 +20%
            float mult = 1f + (float)(Math.log(currentSpeed / 64f) / Math.log(2)) * 0.2f;
            Vec3 vel = proj.getDeltaMovement().reverse().scale(mult);
            proj.setDeltaMovement(vel);
            // 不消耗盾牌耐久
            event.setShieldDamage(0f);
        }
    }

    private static boolean isBlockingWithCogwheelShield(Player player) {
        if (!player.isUsingItem()) return false;
        ItemStack using = player.getUseItem();
        return using.getItem() == CogwheelshieldItems.COGWHEEL_SHIELD.get()
                && using.getUseAnimation() == UseAnim.BLOCK;
    }
}
