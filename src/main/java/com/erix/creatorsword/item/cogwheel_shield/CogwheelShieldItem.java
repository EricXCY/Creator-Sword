package com.erix.creatorsword.item.cogwheel_shield;

import com.simibubi.create.AllBlocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CogwheelShieldItem extends BaseCogwheelShieldItem {
    public CogwheelShieldItem(Properties properties) {
        super(properties.durability(336));
    }

    @Override
    public BaseCogwheelShieldEntity createThrownEntity(
            Level level,
            LivingEntity owner,
            float speed,
            ItemStack stack
    ) {
        return new CogwheelShieldEntity(level, owner, speed, stack);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(AllBlocks.SHAFT.asItem());
    }
}