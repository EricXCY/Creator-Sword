package com.erix.creatorsword.client.tooltip;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class CaptureBoxTooltip implements TooltipComponent {
    private final String entityTypeId;
    private final CompoundTag entityNbt;

    public CaptureBoxTooltip(String entityTypeId, CompoundTag entityNbt) {
        this.entityTypeId = entityTypeId;
        this.entityNbt = entityNbt;
    }

    public String getEntityTypeId() {
        return entityTypeId;
    }

    public CompoundTag getEntityNbt() {
        return entityNbt;
    }
}
