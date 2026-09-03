package com.erix.creatorsword.item.flywheel_mace;

import com.erix.creatorsword.CreatorSword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FlywheelMaceRenderer extends CustomRenderedItemModelRenderer {
    private static final PartialModel BODY = PartialModel.of(
            CreatorSword.asResource("item/flywheel_mace/body"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model,
                          PartialItemModelRenderer renderer, ItemDisplayContext context,
                          PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(BODY.get(), light);

        pose.pushPose();

        pose.translate(0, 12 / 16f, 0);
        pose.mulPose(Axis.YP.rotationDegrees((AnimationTickHolder.getRenderTime() * 3f) % 360f));
        pose.scale(0.21199783f, 0.189238f, 0.20061834f);
        renderer.render(AllPartialModels.FLYWHEEL.get(), light);
        pose.popPose();
    }
}
