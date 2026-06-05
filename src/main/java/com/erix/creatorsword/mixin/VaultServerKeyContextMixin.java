package com.erix.creatorsword.mixin;

import com.erix.creatorsword.item.trial_key.PrecisionTrialKeyContext;
import com.erix.creatorsword.item.trial_key.PrecisionTrialKeyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VaultBlockEntity.Server.class)
public class VaultServerKeyContextMixin {

    @Inject(method = "tryInsertKey", at = @At("HEAD"))
    private static void creatorsword$storeCurrentKey(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            VaultConfig config,
            VaultServerData serverData,
            VaultSharedData sharedData,
            Player player,
            ItemStack stack,
            CallbackInfo ci
    ) {
        if (PrecisionTrialKeyUtil.isPrecisionKey(stack)) {
            PrecisionTrialKeyContext.set(player.getUUID(), stack);
        }
    }

    @Inject(method = "tryInsertKey", at = @At("RETURN"))
    private static void creatorsword$clearCurrentKey(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            VaultConfig config,
            VaultServerData serverData,
            VaultSharedData sharedData,
            Player player,
            ItemStack stack,
            CallbackInfo ci
    ) {
        if (PrecisionTrialKeyUtil.isPrecisionKey(stack)) {
            PrecisionTrialKeyContext.clear();
        }
    }
}