package com.erix.creatorsword.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CaptureBoxTooltipClient implements ClientTooltipComponent {
    private final String entityTypeId;
    private final CompoundTag entityNbt;

    public CaptureBoxTooltipClient(String entityTypeId, CompoundTag entityNbt) {
        this.entityTypeId = entityTypeId;
        this.entityNbt = entityNbt;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return 80;
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel clientLevel = mc.level;
        if (clientLevel == null) return;

        ResourceLocation rl = ResourceLocation.tryParse(this.entityTypeId);
        if (rl == null) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
        if (type == null) return;

        Entity e = type.create(clientLevel);
        if (!(e instanceof LivingEntity living)) return;

        try {
            living.setOnGround(true);
            living.load(this.entityNbt);
        } catch (Exception ex) {
            return;
        }

        int w = getWidth(font);
        int h = getHeight();
        int textY = pY;
        boolean showName = living.hasCustomName();

        if (showName) {
            guiGraphics.drawString(font, living.getCustomName(), pX, textY, 0xFFFFFF);
            textY += 10;
        }

        float health = living.getHealth();
        Component healthText = Component.literal("Health: " + String.format("%.1f", health));
        guiGraphics.drawString(font, healthText, pX, textY, 0xAAAAFF);

        int pad = 2;
        int left   = pX + pad;
        int top    = pY + pad;
        int right  = pX + w - pad;
        int bottom = pY + h - pad;

        int textH = showName ? 22 : 12;

        int modelTop = top + textH;

        int modelAreaW = right - left;
        int modelAreaH = bottom - modelTop;

        living.refreshDimensions();

        float visualH = living.getBbHeight() * 1.35f;
        float visualW = living.getBbWidth() * 1.60f;

        float maxDim = Math.max(visualH, visualW);
        float targetH = (modelAreaH - 2) * 0.95f;
        int modelSize = Mth.clamp((int)(targetH / maxDim), 10, 40);

        int posX = left + modelAreaW / 2;
        int missingLinePx = showName ? 0 : 5;
        int posY = bottom - 2 - missingLinePx;

        guiGraphics.enableScissor(left, modelTop, right, bottom);

        double rot = System.currentTimeMillis() / 25.0D % 360.0D;
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        pose.mul(new Quaternionf().rotateY((float) Math.toRadians(rot)));

        InventoryScreen.renderEntityInInventory(
                guiGraphics,
                posX,
                posY,
                (float) modelSize,
                new Vector3f(),
                pose,
                null,
                living
        );
        guiGraphics.disableScissor();
    }

}
