package com.erix.creatorsword.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
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
        ItemStack itemStack = player.getItemInHand(hand);

        if (!itemStack.is(Tags.Items.TOOLS_WRENCH)) {
            return 0;
        }

        BlockHitResult blockHitResult = FTBUltiminePlayerData.rayTrace(player) instanceof BlockHitResult hit
                ? hit
                : null;

        if (blockHitResult == null) {
            return 0;
        }

        var playerData = FTBUltimine.instance.getOrCreatePlayerData(player);
        boolean wasPressed = playerData.isPressed();

        int didWork = 0;

        playerData.setPressed(false);

        try {
            for (BlockPos pos : positions) {
                if (itemStack.isEmpty()) {
                    break;
                }

                if (!player.level().isLoaded(pos)) {
                    continue;
                }

                if (player.level().getBlockState(pos).isAir()) {
                    continue;
                }

                BlockHitResult currentHitResult = blockHitResult.withPosition(pos);
                UseOnContext context = new UseOnContext(player, hand, currentHitResult);

                InteractionResult result = itemStack.useOn(context);

                if (result != InteractionResult.SUCCESS) {
                    continue;
                }

                didWork++;
            }
        } finally {
            playerData.setPressed(wasPressed);
        }

        return didWork;
    }
}