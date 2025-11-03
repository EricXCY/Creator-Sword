package com.erix.creatorsword.item.supreme_glue;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.AllKeys;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHelper;
import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.RaycastHelper.PredicateTraceResult;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

import java.util.Set;

//still in test

@EventBusSubscriber(value = Dist.CLIENT, modid = CreatorSword.MODID)
public class SupremeGlueScrollHandler {

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        // 仅当手持 Supreme Glue 且按下 Ctrl 时
        if (!(player.getMainHandItem().is(SupremeGlueItem.SUPREME_GLUE))) return;
        if (!AllKeys.ctrlDown()) return;

        event.setCanceled(true);

        double delta = event.getScrollDeltaY();
        if (delta == 0) return;

        SuperGlueSelectionHandler handler = CreateClient.GLUE_HANDLER;
        if (handler == null) return;

        try {
            var firstField = SuperGlueSelectionHandler.class.getDeclaredField("firstPos");
            var hoveredField = SuperGlueSelectionHandler.class.getDeclaredField("hoveredPos");
            var clusterField = SuperGlueSelectionHandler.class.getDeclaredField("currentCluster");
            firstField.setAccessible(true);
            hoveredField.setAccessible(true);
            clusterField.setAccessible(true);

            BlockPos first = (BlockPos) firstField.get(handler);
            BlockPos hovered = (BlockPos) hoveredField.get(handler);
            if (first == null || hovered == null) return;

            // 构造当前 AABB
            AABB bb = new AABB(Vec3.atLowerCornerOf(first), Vec3.atLowerCornerOf(hovered)).expandTowards(1, 1, 1);

            Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
            boolean inside = bb.contains(cam);
            final AABB finalBb = bb;
            PredicateTraceResult faceHit =
                    RaycastHelper.rayTraceUntil(player, 70, pos -> inside ^ finalBb.contains(VecHelper.getCenterOf(pos)));
            Direction selectedFace = faceHit.missed() ? null : (inside ? faceHit.getFacing().getOpposite() : faceHit.getFacing());
            if (selectedFace == null) return;

            // 内部则反向
            if (inside) delta *= -1;

            int intDelta = (int) (delta > 0 ? Math.ceil(delta) : Math.floor(delta));
            if (intDelta == 0) intDelta = delta > 0 ? 1 : -1;

            Vec3i normal = selectedFace.getNormal();
            int x = normal.getX() * intDelta;
            int y = normal.getY() * intDelta;
            int z = normal.getZ() * intDelta;

            AxisDirection axDir = selectedFace.getAxisDirection();
            if (axDir == AxisDirection.NEGATIVE)
                bb = bb.move(-x, -y, -z);

            double maxX = Math.max(bb.maxX - x * axDir.getStep(), bb.minX);
            double maxY = Math.max(bb.maxY - y * axDir.getStep(), bb.minY);
            double maxZ = Math.max(bb.maxZ - z * axDir.getStep(), bb.minZ);
            bb = new AABB(bb.minX, bb.minY, bb.minZ, maxX, maxY, maxZ);

            // 更新 hoveredPos
            BlockPos newHovered = BlockPos.containing(bb.maxX, bb.maxY, bb.maxZ);
            hoveredField.set(handler, newHovered);

            // ⚙️ 重新计算 glue 群集 (Create 渲染选框依赖这个)
            Set<BlockPos> newCluster = SuperGlueSelectionHelper.searchGlueGroup(mc.level, first, newHovered, true);
            clusterField.set(handler, newCluster);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
