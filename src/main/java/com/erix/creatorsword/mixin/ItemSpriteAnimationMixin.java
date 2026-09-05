package com.erix.creatorsword.mixin;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.compat.render.ItemSpriteAnimationCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemSpriteAnimationMixin {
    @Inject(method = "renderQuadList", at = @At("HEAD"))
    private void creatorsword$activateAnimatedSprites(PoseStack poseStack, VertexConsumer consumer,
                                                    List<BakedQuad> quads, ItemStack stack,
                                                    int light, int overlay, CallbackInfo ci) {
        if (!stack.isEmpty() && CreatorSword.MODID.equals(
                BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace())) {
            // Track the actual rendered quads, including foil, all model passes and resource-pack textures.
            ItemSpriteAnimationCompat.markActive(quads);
        }
    }
}
