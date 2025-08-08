package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;

public abstract class BaseSwordItemRenderer extends CustomRenderedItemModelRenderer {

    protected abstract PartialModel getSwordModel();
    protected abstract PartialModel getGearModel();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        float rotationAngle = ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks());
        ms.pushPose();

        switch (transformType) {
            case THIRD_PERSON_RIGHT_HAND -> {
                ms.mulPose(Axis.YP.rotationDegrees(-174));
                ms.mulPose(Axis.ZP.rotationDegrees(6));
                ms.translate(1 / 16f, 1.75 / 16f, 0.75 / 16f);
            }
            case THIRD_PERSON_LEFT_HAND -> {
                ms.mulPose(Axis.YP.rotationDegrees(-174));
                ms.mulPose(Axis.ZP.rotationDegrees(-6));
                ms.translate(-1 / 16f, 1.75 / 16f, 0.75 / 16f);
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                ms.mulPose(Axis.XP.rotationDegrees(-4.5f));
                ms.mulPose(Axis.YP.rotationDegrees(180));
                ms.mulPose(Axis.ZP.rotationDegrees(10));
                ms.translate(-1 / 16f, 0.75 / 16f, 0.25 / 16f);
                ms.scale(0.85f, 0.85f, 0.85f);
            }
            case FIRST_PERSON_LEFT_HAND -> {
                ms.mulPose(Axis.XP.rotationDegrees(-4.5f));
                ms.mulPose(Axis.YP.rotationDegrees(180));
                ms.mulPose(Axis.ZP.rotationDegrees(-10));
                ms.translate(1 / 16f, 0.75 / 16f, 0.25 / 16f);
                ms.scale(0.85f, 0.85f, 0.85f);
            }
            case GROUND -> {
                ms.mulPose(Axis.XP.rotationDegrees(-90));
                ms.mulPose(Axis.YP.rotationDegrees(-124));
                ms.scale(0.76914f, 0.76914f, 0.76914f);
            }
            case GUI -> {
                ms.mulPose(Axis.XP.rotationDegrees(28));
                ms.mulPose(Axis.YP.rotationDegrees(-163));
                ms.mulPose(Axis.ZP.rotationDegrees(43));
                ms.translate(1 / 16f, -2.25 / 16f, 0);
                ms.scale(0.85f, 0.85f, 0.85f);
            }
            case HEAD -> {
                ms.mulPose(Axis.XP.rotationDegrees(143));
                ms.translate(0, 13 / 16f, -6 / 16f);
            }
            case FIXED -> {
                ms.mulPose(Axis.YP.rotationDegrees(90));
                ms.translate(0.5 / 16f, 0.5 / 16f, -0.75 / 16f);
            }
            default -> {}
        }

        // 渲染剑和齿轮
        renderer.render(getSwordModel().get(), light);

        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        renderer.render(getGearModel().get(), light);
        ms.popPose();

        ms.popPose();
    }
}

