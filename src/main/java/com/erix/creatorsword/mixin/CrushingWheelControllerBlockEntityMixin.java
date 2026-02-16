package com.erix.creatorsword.mixin;

import com.erix.creatorsword.item.capture_box.CaptureBoxItem;
import com.erix.creatorsword.item.capture_box.CaptureBoxLoot;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CrushingWheelControllerBlockEntity.class)
public abstract class CrushingWheelControllerBlockEntityMixin {

    @Shadow public ProcessingInventory inventory;

    @Inject(method = "applyRecipe", at = @At("HEAD"), cancellable = true)
    private void creatorsword$applyCaptureBoxLoot(CallbackInfo ci) {
        var self = (CrushingWheelControllerBlockEntity) (Object) this;
        if (!(self.getLevel() instanceof ServerLevel sl))
            return;

        ItemStack input0 = inventory.getStackInSlot(0);
        if (input0.isEmpty())
            return;

        if (!(input0.getItem() instanceof CaptureBoxItem))
            return;
        if (!CaptureBoxItem.hasCapturedMob(input0))
            return;

        List<ItemStack> out = new ArrayList<>();

        List<ItemStack> drops = CaptureBoxLoot.rollLoot(
                sl,
                input0,
                CreateDamageSources.crush(sl),
                null,
                null,
                null,
                null
        );

        for (ItemStack s : drops)
            ItemHelper.addToList(s, out);

        inventory.clear();
        for (int i = 0; i < out.size() && i + 1 < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i + 1, out.get(i));
        }

        ci.cancel();
    }
}
