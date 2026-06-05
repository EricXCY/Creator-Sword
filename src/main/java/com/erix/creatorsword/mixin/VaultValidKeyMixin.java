package com.erix.creatorsword.mixin;

import com.erix.creatorsword.item.PrecisionTrialKeyUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VaultBlockEntity.Server.class)
public class VaultValidKeyMixin {

    @Inject(method = "isValidToInsert", at = @At("HEAD"), cancellable = true)
    private static void creatorsword$acceptPrecisionTrialKeys(
            VaultConfig config,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (PrecisionTrialKeyUtil.matchesVaultKey(config, stack)) {
            cir.setReturnValue(true);
        }
    }
}