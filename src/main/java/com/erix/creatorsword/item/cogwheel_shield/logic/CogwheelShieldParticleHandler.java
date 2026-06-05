package com.erix.creatorsword.item.cogwheel_shield.logic;

import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class CogwheelShieldParticleHandler {
    private static final double FORWARD_OFFSET = 0.3;
    private static final double SIDE_OFFSET = 0.5;
    private static final double DOWN_OFFSET = -0.6;

    private static final double BASE_RADIUS = 0.04;
    private static final double RANDOM_RADIUS = 0.05;

    private static final double BASE_SPEED = 0.06;
    private static final double RANDOM_SPEED = 0.04;

    private static final int BASE_COUNT = 3;
    private static final int MAX_COUNT = 5;

    private CogwheelShieldParticleHandler() {
    }

    public static void spawnOffhandReadyParticles(LocalPlayer player, float speed, float threshold) {
        Level level = player.level();

        Vec3 center = getOffhandCenter(player);
        Vec3 look = player.getLookAngle().normalize();

        Vec3 right = new Vec3(-look.z, 0, look.x);

        if (right.lengthSqr() < 1.0E-4)
            right = new Vec3(1, 0, 0);

        right = right.normalize();

        Vec3 up = right.cross(look).normalize();

        float strength = Mth.clamp(speed / Math.max(threshold, 1f), 1f, 2.5f);
        int count = Mth.clamp((int) (BASE_COUNT * strength), BASE_COUNT, MAX_COUNT);

        SteamJetParticleData steam = new SteamJetParticleData();

        for (int i = 0; i < count; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double radius = BASE_RADIUS + level.random.nextDouble() * RANDOM_RADIUS;

            Vec3 ringOffset = right.scale(Math.cos(angle) * radius)
                    .add(up.scale(Math.sin(angle) * radius));

            Vec3 outward = ringOffset.normalize()
                    .add(look.scale(0.3))
                    .normalize();

            double speedOut = BASE_SPEED + level.random.nextDouble() * RANDOM_SPEED * strength;

            level.addParticle(
                    steam,
                    center.x + ringOffset.x,
                    center.y + ringOffset.y,
                    center.z + ringOffset.z,
                    outward.x * speedOut,
                    outward.y * speedOut,
                    outward.z * speedOut
            );
        }
    }

    private static Vec3 getOffhandCenter(LocalPlayer player) {
        Vec3 look = player.getLookAngle().normalize();

        Vec3 right = new Vec3(-look.z, 0, look.x);

        if (right.lengthSqr() < 1.0E-4)
            right = new Vec3(1, 0, 0);

        right = right.normalize();

        boolean offhandOnRight = player.getMainArm().getOpposite() == HumanoidArm.RIGHT;
        double side = offhandOnRight ? SIDE_OFFSET : -SIDE_OFFSET;

        return player.getEyePosition()
                .add(look.scale(FORWARD_OFFSET))
                .add(right.scale(side))
                .add(0, DOWN_OFFSET, 0);
    }
}