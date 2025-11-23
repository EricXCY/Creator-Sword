package com.erix.creatorsword.item.frogport_grapple;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class FrogportGrappleItemRender extends CustomRenderedItemModelRenderer {

    private static final PartialModel BODY = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/frogport_grapple/body")
    );
    private static final PartialModel HEAD_GOGGLES = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/frogport_grapple/head_goggles")
    );

    @Override
    protected void render(ItemStack stack,
                          CustomRenderedItemModel model,
                          PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType,
                          PoseStack ms,
                          MultiBufferSource buffer,
                          int light,
                          int overlay) {

        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = data.copyTag();
        float progress = Mth.clamp(tag.getFloat("FrogTongueProgress"), 0.0f, 1.0f);
        float headPitch = Mth.lerp(progress, 0, 40f);

        ms.pushPose();

        switch (transformType) {
            case GUI -> {
                ms.mulPose(Axis.XP.rotationDegrees(34f));
                ms.mulPose(Axis.YP.rotationDegrees(130f));
                ms.scale(0.6f, 0.6f, 0.6f);
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                ms.translate(5f / 16f, 0, -7f / 16f);
                ms.scale(0.6f, 0.6f, 0.6f);
            }
            case FIRST_PERSON_LEFT_HAND -> {
                ms.translate(-5f / 16f, 0, -7f / 16f);
                ms.scale(0.6f, 0.6f, 0.6f);
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                ms.translate(0f, 1.75f / 16f, 0f);
                ms.scale(0.4f, 0.4f, 0.4f);
            }
            case GROUND -> {
                ms.scale(0.6f, 0.6f, 0.6f);
            }
            case FIXED -> {
                ms.mulPose(Axis.XP.rotationDegrees(-90f));
                ms.mulPose(Axis.YP.rotationDegrees(-180f));
                ms.translate(0f, 0f, -3f / 16f);
            }
            default -> {
                ms.scale(0.5f, 0.5f, 0.5f);
            }
        }

        renderer.render(BODY.get(), light);

        float px = 8f / 16f;
        float py = -3f / 16f;
        float pz = 5f / 16f;

        ms.pushPose();
        ms.translate(px, py, pz);
        ms.mulPose(Axis.XP.rotationDegrees(headPitch));
        ms.translate(-px, -py, -pz);
        renderer.render(HEAD_GOGGLES.get(), light);
        ms.popPose();

        ms.popPose();
    }
}
