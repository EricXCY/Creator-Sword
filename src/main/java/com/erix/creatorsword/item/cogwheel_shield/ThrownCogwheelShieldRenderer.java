package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.entity.ThrownCogwheelShield;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ThrownCogwheelShieldRenderer extends EntityRenderer<ThrownCogwheelShield> {
    private static final PartialModel HANDLE = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/handle")
    );
    private static final PartialModel ROTATING_GEAR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/cogwheel_shield_handless")
    );
    private final ItemRenderer itemRenderer;

    public ThrownCogwheelShieldRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ThrownCogwheelShield entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw + 180.0f));
        poseStack.scale(1.0f, 1.0f, 1.0f);

        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            stack = new ItemStack(CogwheelShieldItems.COGWHEEL_SHIELD.get());
        }

        itemRenderer.render(
                stack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                buffer,
                light,
                OverlayTexture.NO_OVERLAY,
                HANDLE.get()
        );

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRotationAngle()));
        itemRenderer.render(
                stack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                buffer,
                light,
                OverlayTexture.NO_OVERLAY,
                ROTATING_GEAR.get()
        );
        poseStack.popPose();

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownCogwheelShield entity) {
        return ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/cogwheel_shield_handless");
    }
}