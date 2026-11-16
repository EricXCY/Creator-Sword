package com.erix.creatorsword.item.processing;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class WeaponRecipeProcessing {
    private static final Map<Player, Long> LAST_USE = new WeakHashMap<>();

    private WeaponRecipeProcessing() {}

    public static boolean canProcess(Player player) { return run(player, false); }

    public static boolean process(ServerPlayer player) { return run(player, true); }

    private static boolean run(Player player, boolean execute) {
        ProcessingKind kind = ProcessingKind.of(player.getMainHandItem());
        if (kind == null
                || !player.mayBuild() || player.isSpectator() || player.isUsingItem()) return false;
        if (execute && player.level().getGameTime()
                - LAST_USE.getOrDefault(player, Long.MIN_VALUE / 2) < 5) return false;
        var level = player.level();
        var target = ProcessingTarget.find(player);
        if (target == null) return false;
        Entity selected = target.item();
        BlockHitResult blockHit = target.block();
        boolean changed = false;
        if (selected instanceof ItemEntity item) {
            var recipe = kind.findRecipe(player, item.getItem());
            if (recipe.isEmpty()) return false;
            if (!execute) return true;
            RecipeApplier.applyRecipeOn(item, recipe.get().value(), true);
            item.getPersistentData().getCompound("CreateData").remove("Processing");
            changed = true;
        } else if (blockHit.getType() == HitResult.Type.BLOCK) {
            var pos = blockHit.getBlockPos();
            if (!level.mayInteract(player, pos)) return false;
            if (kind == ProcessingKind.PRESS && level.getBlockEntity(pos) instanceof BasinBlockEntity basin) {
                changed = BasinWeaponProcessing.process(basin, execute);
            } else {
                var handler = BlockEntityBehaviour.get(level, pos, TransportedItemStackHandlerBehaviour.TYPE);
                changed = processTransported(player, kind, handler, execute);
            }
        }

        if (changed && execute) {
            LAST_USE.put(player, level.getGameTime());
            player.getMainHandItem().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            level.playSound(null, player.blockPosition(), kind.sound, SoundSource.PLAYERS, .4f, 1.5f);
        }
        return changed;
    }

    private static boolean processTransported(Player player, ProcessingKind kind,
            TransportedItemStackHandlerBehaviour handler, boolean execute) {
        var level = player.level();
        if (handler == null) return false;
        boolean[] processed = {false};
        handler.handleProcessingOnAllItems(input -> {
            if (input.locked || input.lockedExternally) return TransportedResult.doNothing();
            var recipe = kind.findRecipe(player, input.stack);
            if (recipe.isEmpty()) return TransportedResult.doNothing();
            processed[0] = true;
            if (!execute) return TransportedResult.doNothing();
            List<TransportedItemStack> outputs = new ArrayList<>();
            for (ItemStack stack : RecipeApplier.applyRecipeOn(level, input.stack, recipe.get().value(), true)) {
                var output = input.getSimilar();
                output.stack = stack;
                output.processedBy = null;
                output.processingTime = 0;
                outputs.add(output);
            }
            return TransportedResult.convertTo(outputs);
        });
        return processed[0];
    }
}
