package com.erix.creatorsword.item.capture_box;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
public final class CaptureBoxLoot {

    private CaptureBoxLoot() {}

    public static Optional<List<ItemStack>> tryRollLoot(
            ServerLevel level,
            ItemStack captureBox,
            @Nullable DamageSource damageSource,
            @Nullable Entity attackingEntity,
            @Nullable Entity directAttackingEntity,
            @Nullable Player lastDamagePlayer,
            @Nullable ItemStack tool
    ) {
        if (!CaptureBoxItem.hasCapturedMob(captureBox))
            return Optional.empty();

        EntityType<?> type = CaptureBoxItem.getStoredEntityType(captureBox);
        CompoundTag entityNbt = CaptureBoxItem.getCapturedEntityNbt(captureBox);
        if (type == null || entityNbt == null)
            return Optional.empty();

        Entity created = type.create(level);
        if (!(created instanceof LivingEntity living))
            return Optional.empty();
        living.load(entityNbt);

        BlockPos sp = level.getSharedSpawnPos();
        living.moveTo(sp.getX() + 0.5, sp.getY() + 0.5, sp.getZ() + 0.5, 0.0F, 0.0F);

        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, living)
                .withParameter(LootContextParams.ORIGIN, living.position());

        if (damageSource != null)
            builder = builder.withOptionalParameter(LootContextParams.DAMAGE_SOURCE, damageSource);

        if (attackingEntity != null)
            builder = builder.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, attackingEntity);

        if (directAttackingEntity != null)
            builder = builder.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, directAttackingEntity);

        if (lastDamagePlayer != null)
            builder = builder.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, lastDamagePlayer);

        if (tool != null && !tool.isEmpty())
            builder = builder.withOptionalParameter(LootContextParams.TOOL, tool);

        LootParams params = builder.create(LootContextParamSets.ENTITY);

        ResourceKey<LootTable> lootTableId = living.getLootTable();
        LootTable table = level.getServer().reloadableRegistries().getLootTable(lootTableId);

        int rolls = 2;
        List<ItemStack> out = new java.util.ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            out.addAll(table.getRandomItems(params));
        }
        List<ItemStack> merged = new java.util.ArrayList<>();
        outer:
        for (ItemStack s : out) {
            for (ItemStack m : merged) {
                if (ItemStack.isSameItemSameComponents(m, s) && m.getCount() < m.getMaxStackSize()) {
                    int can = Math.min(s.getCount(), m.getMaxStackSize() - m.getCount());
                    m.grow(can);
                    s.shrink(can);
                    if (s.isEmpty()) continue outer;
                }
            }
            merged.add(s.copy());
        }
        return Optional.of(merged);
    }

    public static List<ItemStack> rollLoot(
            ServerLevel level,
            ItemStack captureBox,
            @Nullable DamageSource damageSource,
            @Nullable Entity attackingEntity,
            @Nullable Entity directAttackingEntity,
            @Nullable Player lastDamagePlayer,
            @Nullable ItemStack tool
    ) {
        return tryRollLoot(level, captureBox, damageSource, attackingEntity, directAttackingEntity, lastDamagePlayer, tool)
                .orElse(List.of());
    }

    public static List<ItemStack> rollLoot(ServerLevel level, ItemStack captureBox, @Nullable Player lastDamagePlayer) {
        return rollLoot(level, captureBox, null, null, null, lastDamagePlayer, null);
    }
}
