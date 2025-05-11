package com.erix.creatorsword.item.cogwheel_shield;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class CogwheelshieldItemRenderer extends CustomRenderedItemModelRenderer {

    private static final PartialModel HANDLE = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/cogwheel_shield/handle")
    );
    private static final PartialModel ROTATING_GEAR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/cogwheel_shield/cogwheel_shield_handless")
    );
    // 渲染状态缓存
    private float rotationAngle = 0f;
    private float currentSpeed = 0f;
    private int holdTime = 0;

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        ms.pushPose(); // 第一次PushPose

        // 先根据上下文旋转缩放
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
                boolean blocking = Minecraft.getInstance().player.isUsingItem() &&
                        Minecraft.getInstance().player.getUseItem() == stack;
                if (blocking) {
                    ms.mulPose(Axis.XP.rotationDegrees(275)); // 举起格挡姿势
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
                boolean blocking = Minecraft.getInstance().player.isUsingItem() &&
                        Minecraft.getInstance().player.getUseItem() == stack;
                if (blocking) {
                    ms.mulPose(Axis.XP.rotationDegrees(275)); // 举起格挡姿势
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
                // NONE等，忽略
            }
        }

        // 旋转/缩放完之后，渲染护手
        renderer.render(HANDLE.get(), light);

        // 检测按键加速旋转
        boolean charging = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_TAB);
        if (charging) {
            holdTime++;
            if (holdTime % 60 == 0 && currentSpeed < 256f) {
                currentSpeed = currentSpeed == 0f ? 8f : currentSpeed * 2;
            }
        } else {
            holdTime = 0;
            if (currentSpeed > 0f) {
                // 每秒减半：每20tick减一次
                if (AnimationTickHolder.getTicks() % 40 == 0) {
                    currentSpeed /= 2f;
                    if (currentSpeed < 8f) {
                        currentSpeed = 0f;
                    }
                }
            }
        }

        float delta = AnimationTickHolder.getPartialTicks();
        rotationAngle += currentSpeed * delta / 20f;
        rotationAngle %= 360;

        ms.pushPose(); // 进入齿轮局部旋转
        ms.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        renderer.render(ROTATING_GEAR.get(), light);
        ms.popPose(); // 结束齿轮局部旋转

        ms.popPose(); // 恢复初始Pose
    }

}
