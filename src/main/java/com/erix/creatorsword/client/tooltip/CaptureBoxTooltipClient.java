package com.erix.creatorsword.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CaptureBoxTooltipClient implements ClientTooltipComponent {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 48;
    private static final int PAD = 2;
    private static final int FOOT_LIFT = 6;

    private static final float H_VIS = 1.35f;
    private static final float W_VIS = 2.05f;
    private static final float FILL = 0.95f;
    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 40;

    private final String entityTypeId;
    private final CompoundTag entityNbt;

    private static String LAST_KEY = null;
    private static LivingEntity LAST_ENTITY = null;
    private static ClientLevel LAST_LEVEL = null;

    public CaptureBoxTooltipClient(String entityTypeId, CompoundTag entityNbt) {
        this.entityTypeId = entityTypeId;
        this.entityNbt = entityNbt;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return WIDTH;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics gg) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        LivingEntity living = getOrCreateCachedLiving(level, entityTypeId, entityNbt);
        if (living == null) return;

        int left = x + PAD;
        int top = y + PAD;
        int right = x + WIDTH - PAD;
        int bottom = y + HEIGHT - PAD;

        int areaW = right - left;
        int areaH = bottom - top;

        int modelSize = computeModelSize(living, areaW, areaH);

        int posX = left + areaW / 2;
        int posY = bottom - FOOT_LIFT;

        Quaternionf pose = computePose();

        InventoryScreen.renderEntityInInventory(
                gg,
                posX,
                posY,
                (float) modelSize,
                new Vector3f(),
                pose,
                null,
                living
        );
    }

    private static Quaternionf computePose() {
        double rot = System.currentTimeMillis() / 25.0D % 360.0D;
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        pose.mul(new Quaternionf().rotateY((float) Math.toRadians(rot)));
        return pose;
    }

    private static int computeModelSize(LivingEntity living, int areaW, int areaH) {
        living.refreshDimensions();

        float bbH = living.getBbHeight();
        float bbW = living.getBbWidth();

        float visualH = bbH * H_VIS;
        float visualW = bbW * W_VIS;

        float targetH = (areaH - 2) * FILL;
        float targetW = (areaW - 2) * FILL;

        float sizeByH = targetH / Math.max(visualH, 0.001f);
        float sizeByW = targetW / Math.max(visualW, 0.001f);

        return Mth.clamp((int) Math.min(sizeByH, sizeByW), MIN_SIZE, MAX_SIZE);
    }

    @Nullable
    private static LivingEntity getOrCreateCachedLiving(ClientLevel level, String typeId, CompoundTag nbt) {
        String key = typeId + "#" + nbt.hashCode();

        if (LAST_LEVEL != level) {
            LAST_LEVEL = level;
            LAST_KEY = null;
            LAST_ENTITY = null;
        }

        if (key.equals(LAST_KEY) && LAST_ENTITY != null) {
            return LAST_ENTITY;
        }

        ResourceLocation rl = ResourceLocation.tryParse(typeId);
        if (rl == null) return null;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
        if (type == null) return null;

        Entity e = type.create(level);
        if (!(e instanceof LivingEntity living)) return null;

        try {
            living.setOnGround(true);
            living.load(nbt);
        } catch (Exception ignored) {
            return null;
        }

        LAST_KEY = key;
        LAST_ENTITY = living;
        return living;
    }
}
