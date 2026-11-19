package com.erix.creatorsword.block.sword_pedestal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.tags.ItemTags;

public class SwordPedestalRenderer extends KineticBlockEntityRenderer<SwordPedestalBlockEntity> {
    public SwordPedestalRenderer(BlockEntityRendererProvider.Context context) { super(context); }
    @Override protected void renderSafe(SwordPedestalBlockEntity be, float partialTicks, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        var state = be.getBlockState();
        var facing = state.getValue(SwordPedestalBlock.FACING);
        var target = buffers.getBuffer(RenderType.cutoutMipped());
        renderRotatingBuffer(be, CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state, facing), pose, target, light);
        if (state.getValue(SwordPedestalBlock.BOTTOM_SHAFT))
            renderRotatingBuffer(be, CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, facing.getOpposite()), pose, target, light);
        var item = be.getHeldItem();
        if (item.isEmpty()) return;
        pose.pushPose();
        pose.translate(.5, .5, .5);
        pose.mulPose(Axis.YP.rotationDegrees(-SwordPedestalBlock.modelYRotation(state)));
        pose.mulPose(Axis.XP.rotationDegrees(-SwordPedestalBlock.modelXRotation(state)));
        if (SwordPedestalBlock.modelRolled(state)) pose.mulPose(Axis.YP.rotationDegrees(-90));
        pose.translate(-.5, -.5, -.5);
        if (!item.is(ItemTags.SWORDS)) {
            pose.translate(.5 - .1 / 16, 11 / 16.0 + .5, .5);
        } else {
            pose.translate(.5, 1.375, .5);
            pose.mulPose(Axis.YP.rotationDegrees(90));
            pose.mulPose(Axis.ZP.rotationDegrees(180));
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.NONE, light, overlay,
                pose, buffers, be.getLevel(), 0);
        pose.popPose();
    }
}
