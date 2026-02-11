package com.erix.creatorsword.item.frogport_grapple;

import com.erix.creatorsword.CreatorSword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = CreatorSword.MODID, value = Dist.CLIENT)
public class FrogportGrappleTongueRenderer {

    private static final String KEY_HOOKED      = "FrogHooked";
    private static final String KEY_PROGRESS    = "FrogTongueProgress";
    private static final String KEY_ENTITY_ID   = "FrogHookEntityId";
    private static final String KEY_HOOK_X      = "FrogHookX";
    private static final String KEY_HOOK_Y      = "FrogHookY";
    private static final String KEY_HOOK_Z      = "FrogHookZ";
    private static final String KEY_PROGRESS_PREV = "FrogTongueProgressPrev";

    private static final ResourceLocation TONGUE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreatorSword.MODID, "textures/misc/tongue.png");

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        float partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        MultiBufferSource buffer = mc.renderBuffers().bufferSource();
        Camera camera = event.getCamera();

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof FrogportGrappleItem)) continue;

            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = data.copyTag();

            boolean hooked = tag.getBoolean(KEY_HOOKED);
            if (!hooked) continue;

            float curr = Mth.clamp(tag.getFloat(KEY_PROGRESS), 0f, 1f);
            float prev = Mth.clamp(tag.getFloat(KEY_PROGRESS_PREV), 0f, 1f);

            if (!tag.contains(KEY_PROGRESS_PREV)) {
                prev = curr;
            }

            float renderProgress = Mth.lerp(partialTicks, prev, curr);
            renderProgress = Mth.clamp(renderProgress, 0f, 1f);

            if (renderProgress <= 0f) continue;

            Vec3 hookPos = getHookEndPos(tag, partialTicks);
            if (hookPos == null) continue;

            Vec3 start = getTongueStart(player, hand, partialTicks);
            Vec3 direction = hookPos.subtract(start);
            if (direction.lengthSqr() < 1e-4) continue;

            Vec3 end = start.add(direction.scale(renderProgress));

            drawTongue(start, end, event.getPoseStack(), buffer, camera);
        }
    }


    private static Vec3 getTongueStart(LocalPlayer player, InteractionHand hand, float partialTick) {
        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 look = player.getViewVector(partialTick);
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();

        HumanoidArm mainArm = player.getMainArm();
        HumanoidArm armForHand = (hand == InteractionHand.MAIN_HAND)
                ? mainArm
                : mainArm.getOpposite();

        double sideSign = (armForHand == HumanoidArm.RIGHT) ? 1.0 : -1.0;

        Vec3 base = eye
                .add(look.scale(0.7))  // 前后
                .add(0, -0.27, 0);      // 上下

        double offset = 0.4; // 左右身位偏移
        return base.add(right.scale(sideSign * offset));
    }


    @Nullable
    private static Vec3 getHookEndPos(CompoundTag tag, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        int entityId = tag.getInt(KEY_ENTITY_ID);
        if (entityId != 0 && mc.level != null) {
            Entity e = mc.level.getEntity(entityId);
            if (e != null) {
                double x = Mth.lerp(partialTicks, e.xOld, e.getX());
                double y = Mth.lerp(partialTicks, e.yOld, e.getY()) + e.getBbHeight() * 0.5;
                double z = Mth.lerp(partialTicks, e.zOld, e.getZ());
                return new Vec3(x, y, z);
            }
        }

        if (tag.contains(KEY_HOOK_X)) {
            double hx = tag.getDouble(KEY_HOOK_X);
            double hy = tag.getDouble(KEY_HOOK_Y);
            double hz = tag.getDouble(KEY_HOOK_Z);
            return new Vec3(hx, hy, hz);
        }

        return null;
    }

    private static void drawTongue(Vec3 start, Vec3 end,
                                   PoseStack poseStack,
                                   MultiBufferSource buffer,
                                   Camera camera) {

        Vec3 camPos = camera.getPosition();
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-4) return;

        Vec3 side = dir.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1e-4) {
            side = dir.cross(new Vec3(1, 0, 0));
        }

        double halfWidth = 0.06;
        side = side.normalize().scale(halfWidth);

        Vec3 s1 = start.add(side);
        Vec3 s2 = start.subtract(side);
        Vec3 e1 = end.add(side);
        Vec3 e2 = end.subtract(side);

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TONGUE_TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        Vec3 normal = dir.cross(side).normalize();
        float nx = (float) normal.x;
        float ny = (float) normal.y;
        float nz = (float) normal.z;

        int light = 0xF000F0;
        int r = 255;
        int g = 255;
        int b = 255;
        int a = 255;

        float U0 = 0f, U1 = 1f, V0 = 0f, V1 = 1f;

        // 正面
        put(vc, pose, s1, r, g, b, a, light, nx, ny, nz, U0, V0);
        put(vc, pose, s2, r, g, b, a, light, nx, ny, nz, U1, V0);
        put(vc, pose, e2, r, g, b, a, light, nx, ny, nz, U1, V1);
        put(vc, pose, e1, r, g, b, a, light, nx, ny, nz, U0, V1);

        // 背面
        put(vc, pose, s1, r, g, b, a, light, -nx, -ny, -nz, U0, V0);
        put(vc, pose, e1, r, g, b, a, light, -nx, -ny, -nz, U0, V1);
        put(vc, pose, e2, r, g, b, a, light, -nx, -ny, -nz, U1, V1);
        put(vc, pose, s2, r, g, b, a, light, -nx, -ny, -nz, U1, V0);

        poseStack.popPose();
    }

    private static void put(VertexConsumer vc, PoseStack.Pose pose,
                            Vec3 v, int r, int g, int b, int a,
                            int light, float nx, float ny, float nz,
                            float u, float vTex) {

        int overlay = OverlayTexture.NO_OVERLAY;
        int overlayU = overlay & 0xFFFF;
        int overlayV = (overlay >> 16) & 0xFFFF;

        int lightU = light & 0xFFFF;
        int lightV = (light >> 16) & 0xFFFF;

        vc.addVertex(pose, (float) v.x, (float) v.y, (float) v.z)
                .setColor(r, g, b, a)
                .setUv(u, vTex)
                .setUv1(overlayU, overlayV)
                .setUv2(lightU, lightV)
                .setNormal(pose, nx, ny, nz);
    }
}
