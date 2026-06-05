package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.item.cogwheel_shield.logic.CogwheelShieldKeyInputHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseCogwheelShieldRenderer extends CustomRenderedItemModelRenderer {
    private static final Map<ResourceLocation, PartialModel> MODEL_CACHE = new ConcurrentHashMap<>();

    private final PartialModel handleModel;
    private final PartialModel rotatingGearModel;

    public BaseCogwheelShieldRenderer(BaseCogwheelShieldItem item) {
        this.handleModel = getCachedModel(item.getHandleModelLocation());
        this.rotatingGearModel = getCachedModel(item.getRotatingGearModelLocation());
    }

    private static PartialModel getCachedModel(ResourceLocation location) {
        return MODEL_CACHE.computeIfAbsent(location, PartialModel::of);
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms,
                          MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();

        applyTransform(stack, transformType, ms);

        renderer.render(handleModel.get(), light);

        float rotationAngle = getVisualRotationAngle(stack, transformType);

        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        renderer.render(rotatingGearModel.get(), light);
        ms.popPose();

        ms.popPose();
    }

    private void applyTransform(ItemStack stack, ItemDisplayContext transformType, PoseStack ms) {
        switch (transformType) {
            case THIRD_PERSON_RIGHT_HAND -> {
                ms.mulPose(Axis.YP.rotationDegrees(90));
                ms.translate(-1f / 16f, 2 / 16f, 1 / 16f);
                ms.scale(0.6f, 0.6f, 0.6f);
            }
            case THIRD_PERSON_LEFT_HAND -> {
                ms.mulPose(Axis.YP.rotationDegrees(90));
                ms.translate(-1f / 16f, 2 / 16f, -1 / 16f);
                ms.scale(0.6f, 0.6f, 0.6f);
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                boolean blocking = isBlockingWith(stack);

                if (blocking) {
                    ms.mulPose(Axis.XP.rotationDegrees(275));
                    ms.mulPose(Axis.YP.rotationDegrees(90));
                    ms.translate(8 / 16f, 4f / 16f, 4 / 16f);
                    ms.scale(0.9f, 0.25f, 0.9f);
                } else {
                    ms.mulPose(Axis.XP.rotationDegrees(280));
                    ms.mulPose(Axis.YP.rotationDegrees(90));
                    ms.translate(8 / 16f, 2.5f / 16f, 5 / 16f);
                    ms.scale(0.9f, 0.25f, 0.9f);
                }
            }
            case FIRST_PERSON_LEFT_HAND -> {
                boolean blocking = isBlockingWith(stack);

                if (blocking) {
                    ms.mulPose(Axis.XP.rotationDegrees(275));
                    ms.mulPose(Axis.YP.rotationDegrees(90));
                    ms.translate(8 / 16f, 4f / 16f, -4 / 16f);
                    ms.scale(0.9f, 0.25f, 0.9f);
                } else {
                    ms.mulPose(Axis.XP.rotationDegrees(280));
                    ms.mulPose(Axis.YP.rotationDegrees(90));
                    ms.translate(8 / 16f, 2.5f / 16f, -5 / 16f);
                    ms.scale(0.9f, 0.25f, 0.9f);
                }
            }
            case GROUND -> {
                ms.mulPose(Axis.XP.rotationDegrees(-180));
                ms.mulPose(Axis.ZP.rotationDegrees(10));
                ms.scale(0.5f, 0.5f, 0.5f);
            }
            case GUI -> {
                ms.mulPose(Axis.XP.rotationDegrees(76));
                ms.mulPose(Axis.ZP.rotationDegrees(29));
                ms.scale(0.5f, 0.5f, 0.5f);
            }
            case HEAD -> {
                ms.translate(0, 5.75f / 16f, 0);
                ms.scale(0.75f, 0.75f, 0.75f);
            }
            case FIXED -> {
                ms.mulPose(Axis.XP.rotationDegrees(-90));
                ms.scale(0.7f, 0.7f, 0.7f);
            }
            default -> {
            }
        }
    }

    private boolean isBlockingWith(ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;

        return player != null
                && player.isUsingItem()
                && player.getUseItem().is(stack.getItem());
    }

    private float getVisualRotationAngle(ItemStack stack, ItemDisplayContext transformType) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return 0f;

        return switch (transformType) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND ->
                    CogwheelShieldKeyInputHandler.getOffhandAngle();

            case FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND ->
                    CogwheelShieldKeyInputHandler.getMainhandAngle();

            default -> 0f;
        };
    }
}