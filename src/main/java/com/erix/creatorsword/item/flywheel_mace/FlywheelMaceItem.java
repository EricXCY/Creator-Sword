package com.erix.creatorsword.item.flywheel_mace;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class FlywheelMaceItem extends MaceItem {
    public FlywheelMaceItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).durability(500)
                .component(DataComponents.TOOL, MaceItem.createToolProperties())
                .attributes(MaceItem.createAttributes()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new FlywheelMaceRenderer()));
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreatorSword.MODID);
    public static final DeferredItem<FlywheelMaceItem> FLYWHEEL_MACE =
            ITEMS.registerItem("flywheel_mace", FlywheelMaceItem::new);
}
