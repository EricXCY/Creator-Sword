package com.erix.creatorsword.mixin;

import com.simibubi.create.AllKeys;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchHandler;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RadialWrenchHandler.class)
public class RadialWrenchHandlerMixin {
    @Inject(method = "onKeyInput", at = @At("HEAD"), cancellable = true)
    private static void allowCustomWrenches(int key, boolean pressed, CallbackInfo ci) {
        if (!pressed)
            return;

        if (!com.simibubi.create.AllKeys.ROTATE_MENU.doesModifierAndCodeMatch(key))
            return;

        if (RadialWrenchHandler.COOLDOWN > 0)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        LocalPlayer player = mc.player;
        if (player == null)
            return;

        Level level = player.level();
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (!heldItem.is(Tags.Items.TOOLS_WRENCH))
            return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult bhr))
            return;

        BlockState state = level.getBlockState(bhr.getBlockPos());

        RadialWrenchMenu.tryCreateFor(state, bhr.getBlockPos(), level)
                .ifPresent(ScreenOpener::open);

        ci.cancel();
    }
}
