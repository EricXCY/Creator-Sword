package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.data.ModDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CogwheelShieldItemRenderer extends CustomRenderedItemModelRenderer {

    private static final PartialModel HANDLE = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/cogwheel_shield/handle")
    );
    private static final PartialModel ROTATING_GEAR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/cogwheel_shield/cogwheel_shield_handless")
    );

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        ms.pushPose(); // 第一次PushPose

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
                boolean blocking = false;
                if (Minecraft.getInstance().player != null) {
                    blocking = Minecraft.getInstance().player.isUsingItem() &&
                            Minecraft.getInstance().player.getUseItem() == stack;
                }
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
                boolean blocking = false;
                if (Minecraft.getInstance().player != null) {
                    blocking = Minecraft.getInstance().player.isUsingItem() &&
                            Minecraft.getInstance().player.getUseItem() == stack;
                }
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

        float partialTicks = AnimationTickHolder.getPartialTicks();
        float tickTime = 0;
        if (Minecraft.getInstance().level != null) {
            tickTime = Minecraft.getInstance().level.getGameTime() + partialTicks;
        }

        float speed = stack.getOrDefault(ModDataComponents.GEAR_SHIELD_SPEED.get(), 0f);
        float rotationAngle = (speed * tickTime) % 360f;

        if (Math.abs(rotationAngle) > 0.001f) {
            ms.pushPose();
            ms.mulPose(Axis.YP.rotationDegrees(rotationAngle));
            renderer.render(ROTATING_GEAR.get(), light);
            ms.popPose();
        } else {
            renderer.render(ROTATING_GEAR.get(), light);
        }

        ms.popPose();
    }

}
