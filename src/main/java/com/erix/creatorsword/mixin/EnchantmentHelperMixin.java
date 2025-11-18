package com.erix.creatorsword.mixin;

import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(
            method = "processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void creatorsword$applySturdyAfterUnbreaking(
            ServerLevel level,
            ItemStack stack,
            int originalAmount,
            CallbackInfoReturnable<Integer> cir
    ) {
        int amount = cir.getReturnValue();

        if (amount <= 0) return;

        int sturdyLevel = EnchantmentKeys.getEnchantmentLevel(
                level.registryAccess(),
                EnchantmentKeys.STURDY,
                stack
        );

        if (sturdyLevel <= 0) return;

        if (amount <= sturdyLevel) {
            cir.setReturnValue(0);
        }
    }
}
