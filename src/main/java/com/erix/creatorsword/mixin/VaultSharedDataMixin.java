package com.erix.creatorsword.mixin;

import com.erix.creatorsword.item.PrecisionTrialKeyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

@Mixin(VaultSharedData.class)
public class VaultSharedDataMixin {

    @Shadow
    private Set<UUID> connectedPlayers;

    @Shadow
    boolean isDirty;

    @Inject(method = "updateConnectedPlayersWithinRange", at = @At("RETURN"))
    private void creatorsword$addPrecisionKeyPlayers(
            ServerLevel level,
            BlockPos pos,
            VaultServerData serverData,
            VaultConfig config,
            double range,
            CallbackInfo ci
    ) {
        double rangeSqr = range * range;
        boolean changed = false;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                continue;
            }

            if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > rangeSqr) {
                continue;
            }

            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            boolean hasMatchingPrecisionKey =
                    PrecisionTrialKeyUtil.matchesVaultKey(config, mainHand)
                            || PrecisionTrialKeyUtil.matchesVaultKey(config, offHand);

            if (!hasMatchingPrecisionKey) {
                continue;
            }

            changed |= this.connectedPlayers.add(player.getUUID());
        }

        if (changed) {
            this.isDirty = true;
        }
    }
}