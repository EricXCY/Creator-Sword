package com.erix.creatorsword.item.cogwheel_shield;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CogwheelShieldEntity extends BaseCogwheelShieldEntity {
    public CogwheelShieldEntity(EntityType<? extends CogwheelShieldEntity> type, Level level) {
        super(type, level);
    }

    public CogwheelShieldEntity(Level level, LivingEntity owner, float speed, ItemStack stack) {
        super(CogwheelShieldItems.COGWHEEL_SHIELD_ENTITY.get(), level, owner, speed, stack);
    }

    @Override
    protected Item getFallbackItem() {
        return CogwheelShieldItems.COGWHEEL_SHIELD.get();
    }
}