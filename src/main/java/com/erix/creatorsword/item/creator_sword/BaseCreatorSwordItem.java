//
// Functions of wrench comes from simibubi [https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/content/equipment/wrench/WrenchItem.java]
//

package com.erix.creatorsword.item.creator_sword;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class BaseCreatorSwordItem extends SwordItem {
    public BaseCreatorSwordItem(Tiers tier, Properties attributes) {
        super(tier, attributes);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {
            handleBacktankLogic(player);
        }
        return true;
    }

    private void handleBacktankLogic(Player player) {
        var backtanks = BacktankUtil.getAllWithAir(player);
        if (!backtanks.isEmpty()) {
            int airCost = -100;
            BacktankUtil.consumeAir(player, backtanks.get(0), airCost);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, getRenderer()));
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract CustomRenderedItemModelRenderer getRenderer();

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.mayBuild()) {
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            Block block = state.getBlock();
            if (block instanceof IWrenchable actor) {
                return player.isShiftKeyDown() ? actor.onSneakWrenched(state, context) : actor.onWrenched(state, context);
            } else if (player.isShiftKeyDown() && canWrenchPickup(state)) {
                return onItemUseOnOther(context);
            }
        }
        return super.useOn(context);
    }

    private boolean canWrenchPickup(BlockState state) {
        return AllTags.AllBlockTags.WRENCH_PICKUP.matches(state);
    }

    private InteractionResult onItemUseOnOther(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if (!(world instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        if (player != null && !player.isCreative()) {
            Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand())
                    .forEach(stack -> player.getInventory().placeItemBackInInventory(stack));
        }

        state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
        world.destroyBlock(pos, false);
        AllSoundEvents.WRENCH_REMOVE.playOnServer(world, pos, 1.0F, Create.RANDOM.nextFloat() * 0.5F + 0.5F);
        return InteractionResult.SUCCESS;
    }

    public static void wrenchInstaKillsMinecarts(AttackEntityEvent event) {
        Entity target = event.getTarget();
        if (target instanceof AbstractMinecart minecart) {
            Player player = event.getEntity();
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.is(AllTags.AllItemTags.WRENCH.tag)) {
                if (!player.isCreative()) {
                    minecart.hurt(minecart.damageSources().playerAttack(player), 100.0F);
                }
            }
        }
    }

    public static void playRotateSound(Level level, BlockPos pos) {
        AllSoundEvents.WRENCH_ROTATE.playOnServer(level, pos, 1.0F, Create.RANDOM.nextFloat() + 0.5F);
    }

    public static void playRemoveSound(Level level, BlockPos pos) {
        AllSoundEvents.WRENCH_REMOVE.playOnServer(level, pos, 1.0F, Create.RANDOM.nextFloat() * 0.5F + 0.5F);
    }
}
