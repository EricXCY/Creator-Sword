package com.erix.creatorsword.item.flywheel_mace;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.data.CSDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

public class FlywheelMaceRenderer extends CustomRenderedItemModelRenderer {
    private static final PartialModel BODY = PartialModel.of(
            CreatorSword.asResource("item/flywheel_mace/body"));
    private final Map<Object, SpinState> rotations = new WeakHashMap<>();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model,
                          PartialItemModelRenderer renderer, ItemDisplayContext context,
                          PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(BODY.get(), light);

        pose.pushPose();

        pose.translate(0, 12 / 16f, 0);
        pose.mulPose(Axis.YP.rotationDegrees(getAngle(stack)));
        pose.scale(0.21199783f, 0.189238f, 0.20061834f);
        renderer.render(AllPartialModels.FLYWHEEL.get(), light);
        pose.popPose();
    }

    private float getAngle(ItemStack stack) {
        float time = AnimationTickHolder.getRenderTime();
        float energy = stack.getOrDefault(CSDataComponents.FLYWHEEL_ENERGY.get(), 0f);
        Object key = stack;
        var level = Minecraft.getInstance().level;
        if (level != null) {
            for (Player player : level.players()) {
                if (player.getMainHandItem() != stack) continue;
                key = player;
                break;
            }
        }
        float speed = 1f + 24f * energy;
        SpinState state = rotations.get(key);
        if (state == null) {
            float initialAngle = time + stack.getOrDefault(CSDataComponents.FLYWHEEL_ROTATION.get(), 0f);
            state = new SpinState(initialAngle % 360f, time, speed);
            rotations.put(key, state);
        }
        float elapsed = time - state.time;
        if (elapsed > 0) {
            state.angle = (state.angle + elapsed * (state.speed + speed) * 0.5f) % 360f;
        }
        state.time = time;
        state.speed = speed;
        return state.angle;
    }

    private static final class SpinState {
        private float angle;
        private float time;
        private float speed;

        private SpinState(float angle, float time, float speed) {
            this.angle = angle;
            this.time = time;
            this.speed = speed;
        }
    }
}
