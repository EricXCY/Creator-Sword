package com.erix.creatorsword.item.creator_sword;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreatorSwordItemRenderer extends BaseSwordItemRenderer {

    private static final PartialModel SWORD = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/creator_sword/sword")
    );
    private static final PartialModel ROTATING_GEAR = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath("creatorsword", "item/creator_sword/gear")
    );


    @Override
    protected PartialModel getSwordModel() {
        return SWORD;
    }

    @Override
    protected PartialModel getGearModel() {
        return ROTATING_GEAR;
    }
}
