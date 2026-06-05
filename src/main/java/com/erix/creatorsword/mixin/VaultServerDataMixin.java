package com.erix.creatorsword.mixin;

import com.erix.creatorsword.item.trial_key.PrecisionTrialKeyContext;
import com.erix.creatorsword.item.trial_key.PrecisionTrialKeyUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(VaultServerData.class)
public class VaultServerDataMixin {

    @Inject(method = "hasRewardedPlayer", at = @At("HEAD"), cancellable = true)
    private void creatorsword$bypassRewardedPlayerCheck(
            Player player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        UUID playerId = PrecisionTrialKeyContext.getPlayerId();
        ItemStack key = PrecisionTrialKeyContext.getKey();

        if (playerId == null || key == null) {
            return;
        }

        if (!playerId.equals(player.getUUID())) {
            return;
        }

        if (PrecisionTrialKeyUtil.isPrecisionKey(key)) {
            cir.setReturnValue(false);
        }
    }
}