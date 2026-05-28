package com.erix.creatorsword.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.rightclick.RightClickHandler;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;

import java.util.Collection;

public enum CreatorSwordRightClickHandler implements RightClickHandler {
    INSTANCE;

    @Override
    public int handleRightClickBlock(
            ShapeContext shapeContext,
            InteractionHand hand,
            Collection<BlockPos> positions
    ) {
        var player = shapeContext.player();
        var level = player.level();

        ItemStack itemStack = player.getItemInHand(hand);

        if (!itemStack.is(Tags.Items.TOOLS_WRENCH)) {
            return 0;
        }

        if (!(player.pick(player.blockInteractionRange(), 0.0F, false) instanceof BlockHitResult blockHitResult)) {
            return 0;
        }

        int didWork = 0;

        for (BlockPos pos : positions) {
            if (itemStack.isEmpty()) {
                break;
            }

            if (!level.isLoaded(pos)) {
                continue;
            }

            if (level.getBlockState(pos).isAir()) {
                continue;
            }

            BlockHitResult currentHitResult = blockHitResult.withPosition(pos);
            UseOnContext context = new UseOnContext(player, hand, currentHitResult);

            InteractionResult result = itemStack.useOn(context);

            if (!result.consumesAction()) {
                continue;
            }

            didWork++;
        }

        return didWork;
    }
}