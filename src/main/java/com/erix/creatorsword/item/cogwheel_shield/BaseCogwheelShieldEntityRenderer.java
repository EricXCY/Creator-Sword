package com.erix.creatorsword.item.cogwheel_shield;

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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseCogwheelShieldEntityRenderer<T extends BaseCogwheelShieldEntity> extends EntityRenderer<T> {
    private static final Map<ResourceLocation, PartialModel> MODEL_CACHE = new ConcurrentHashMap<>();

    private final ItemRenderer itemRenderer;

    public BaseCogwheelShieldEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    private static PartialModel getCachedModel(ResourceLocation location) {
        return MODEL_CACHE.computeIfAbsent(location, PartialModel::of);
    }

    @Override
    public void render(T entity, float yaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light) {
        ItemStack stack = entity.getItem();

        if (!(stack.getItem() instanceof BaseCogwheelShieldItem shieldItem)) {
            super.render(entity, yaw, partialTicks, poseStack, buffer, light);
            return;
        }

        poseStack.pushPose();

        applyBaseTransform(entity, yaw, partialTicks, poseStack);

        renderPartial(
                stack,
                getCachedModel(shieldItem.getHandleModelLocation()),
                poseStack,
                buffer,
                light
        );

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRotationAngle(partialTicks)));

        renderPartial(
                stack,
                getCachedModel(shieldItem.getRotatingGearModelLocation()),
                poseStack,
                buffer,
                light
        );

        poseStack.popPose();
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, buffer, light);
    }

    private void applyBaseTransform(T entity, float yaw,
                                    float partialTicks, PoseStack poseStack) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw + 180.0f));
    }

    private void renderPartial(ItemStack stack, PartialModel partialModel,
                               PoseStack poseStack, MultiBufferSource buffer, int light) {
        itemRenderer.render(
                stack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                buffer,
                light,
                OverlayTexture.NO_OVERLAY,
                partialModel.get()
        );
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        ItemStack stack = entity.getItem();

        if (stack.getItem() instanceof BaseCogwheelShieldItem shieldItem)
            return shieldItem.getRotatingGearModelLocation();

        return CogwheelShieldItems.COGWHEEL_SHIELD_GEAR_MODEL;
    }
}