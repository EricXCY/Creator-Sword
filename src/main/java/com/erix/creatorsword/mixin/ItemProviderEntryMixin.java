package com.erix.creatorsword.mixin;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemProviderEntry.class)
public abstract class ItemProviderEntryMixin<R extends ItemLike, T extends R> extends RegistryEntry<R, T> {
    public ItemProviderEntryMixin(AbstractRegistrate owner, DeferredHolder<R, T> key) {
        super(owner, key);
    }

    @Inject(method = "isIn", at = @At("HEAD"), cancellable = true)
    public void isIn(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Tags.Items.TOOLS_WRENCH) && this.get().asItem() instanceof WrenchItem) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}