package com.erix.creatorsword.compat.mixin.simulated;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        targets = "dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock",
        remap = false
)
public abstract class ThrottleLeverBlockMixin {

    @Inject(
            method = "useWithoutItem",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void creatorsword$passWithCreatorSword(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (player.getMainHandItem().is(Tags.Items.TOOLS_WRENCH)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}