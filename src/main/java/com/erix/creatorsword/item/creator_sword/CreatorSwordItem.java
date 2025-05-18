package com.erix.creatorsword.item.creator_sword;

import java.util.function.Consumer;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import javax.annotation.Nonnull;

public class CreatorSwordItem extends SwordItem {

    public CreatorSwordItem(Properties properties) {
        super(Tiers.DIAMOND, new Properties().attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3.1f, -2.3f)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CreatorSwordItemRenderer()));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            // 判断方块是否实现IWrenchable接口
            if (block instanceof IWrenchable wrenchable) {
                if (player.isShiftKeyDown()) {
                    // Shift+右键：拆卸方块
                    return wrenchable.onSneakWrenched(state, context);
                } else {
                    // 右键：旋转方块
                    return wrenchable.onWrenched(state, context);
                }
            }
        }
        // 非Wrenchable方块，则作为普通剑使用
        return super.useOn(context);
    }

    // 矿车秒杀功能
    public static void wrenchInstaKillsMinecarts(AttackEntityEvent event) {
        Entity target = event.getTarget();
        if (target instanceof AbstractMinecart minecart) {
            Player player = event.getEntity();
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof CreatorSwordItem) {
                // 立即破坏矿车
                minecart.hurt(minecart.damageSources().playerAttack(player), 100.0F);
            }
        }
    }

    // 播放旋转声音
    public static void playRotateSound(Level level, BlockPos pos) {
        AllSoundEvents.WRENCH_ROTATE.playOnServer(level, pos, 1.0F, Create.RANDOM.nextFloat() + 0.5F);
    }

    // 播放拆卸声音
    public static void playRemoveSound(Level level, BlockPos pos) {
        AllSoundEvents.WRENCH_REMOVE.playOnServer(level, pos, 1.0F, Create.RANDOM.nextFloat() * 0.5F + 0.5F);
    }
}
