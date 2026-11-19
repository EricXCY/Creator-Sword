package com.erix.creatorsword.block.sword_pedestal.mixin;

import com.erix.creatorsword.block.sword_pedestal.SwordPedestalBlock;

import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(RadialWrenchMenu.class)
public abstract class SwordPedestalRadialWrenchMixin {
    @Shadow private int selectedStateIndex;

    @Inject(method = "cycleAllPropertyValues", at = @At("HEAD"), cancellable = true)
    private void swordPedestalPoses(BlockState state, Property<?> property, List<BlockState> states, CallbackInfo ci) {
        if (!(state.getBlock() instanceof SwordPedestalBlock) || property != SwordPedestalBlock.FACING) return;
        states.clear();
        for (var facing : Direction.values()) {
            states.add(state.setValue(SwordPedestalBlock.FACING, facing).setValue(SwordPedestalBlock.ROTATED, false));
            states.add(state.setValue(SwordPedestalBlock.FACING, facing).setValue(SwordPedestalBlock.ROTATED, true));
        }
        selectedStateIndex = states.indexOf(state);
        ci.cancel();
    }
}
