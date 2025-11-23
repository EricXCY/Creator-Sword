package com.erix.creatorsword.item.frogport_grapple;

import com.simibubi.create.AllSoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FrogportGrappleSounds {
    // 开口（吐舌）
    public static void playExtend(Level level, Player player) {
        if (!level.isClientSide)
            return;
        AllSoundEvents.FROGPORT_OPEN.playAt(level, player.position(), 0.7f, 1.0f, false);
    }

    // 抓到目标
    public static void playLatch(Level level, Player player) {
        if (!level.isClientSide)
            return;
        AllSoundEvents.FROGPORT_CATCH.playAt(level, player.position(), 1.0f, 1.0f, false);
    }

    // 回收舌头
    public static void playRetract(Level level, Player player) {
        if (!level.isClientSide)
            return;
        AllSoundEvents.FROGPORT_CLOSE.playAt(level, player.position(), 1.0f, 1.25f, false);
    }
}
