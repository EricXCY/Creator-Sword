package com.erix.creatorsword.item.cogwheel_shield;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.client.cogwheel_shield.CogwheelShieldKeyInputHandler;
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

public class CogwheelShieldItemRenderer extends CustomRenderedItemModelRenderer {

    private static final PartialModel HANDLE = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/handle")
    );

    private static final PartialModel ROTATING_GEAR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "item/cogwheel_shield/cogwheel_shield_handless")
    );

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        ms.pushPose();

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

        renderer.render(HANDLE.get(), light);

        float rotationAngle = getVisualRotationAngle(stack, transformType);

        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        renderer.render(ROTATING_GEAR.get(), light);
        ms.popPose();

        ms.popPose();
    }

    private static boolean isBlockingWith(ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;

        return player != null
                && player.isUsingItem()
                && player.getUseItem() == stack;
    }

    private static float getVisualRotationAngle(ItemStack stack, ItemDisplayContext transformType) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return 0f;

        return switch (transformType) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND ->
                    CogwheelShieldKeyInputHandler.getOffhandAngle();

            case FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND ->
                    CogwheelShieldKeyInputHandler.getMainhandAngle();

            default -> {
                if (stack == player.getOffhandItem())
                    yield CogwheelShieldKeyInputHandler.getOffhandAngle();

                if (stack == player.getMainHandItem())
                    yield CogwheelShieldKeyInputHandler.getMainhandAngle();

                yield 0f;
            }
        };
    }
}