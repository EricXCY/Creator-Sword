package com.erix.creatorsword.item;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.fluid.CSFluids;
import com.erix.creatorsword.fluid.OminousEssenceClientExtensions;
import com.erix.creatorsword.item.cogwheel_shield.BaseCogwheelShieldItem;
import com.erix.creatorsword.item.cogwheel_shield.BaseCogwheelShieldRenderer;
import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.BaseCreatorSwordItem;
import com.erix.creatorsword.item.creator_sword.BaseCreatorSwordItemRenderer;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.flywheel_mace.FlywheelMaceItem;
import com.erix.creatorsword.item.flywheel_mace.FlywheelMaceRenderer;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItemRender;
import com.erix.creatorsword.item.incomplete_creator_sword.IncompleteCreatorSwordItemRenderer;
import com.erix.creatorsword.item.incomplete_creator_sword.IncompleteCreatorSwordItems;
import com.erix.creatorsword.item.incomplete_creator_sword.IncompleteNetheriteCreatorSwordItemRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.List;

@EventBusSubscriber(modid = CreatorSword.MODID, value = Dist.CLIENT)
public final class ClientExtensions {
    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        for (var entry : CreatorSwordItems.CREATOR_SWORDS) {
            var item = (BaseCreatorSwordItem) entry.get();
            event.registerItem(SimpleCustomRenderer.create(item, new BaseCreatorSwordItemRenderer(item)), item);
        }
        var shield = (BaseCogwheelShieldItem) CogwheelShieldItems.COGWHEEL_SHIELD.get();
        event.registerItem(SimpleCustomRenderer.create(shield, new BaseCogwheelShieldRenderer(shield)), shield);
        var mace = FlywheelMaceItem.FLYWHEEL_MACE.get();
        event.registerItem(SimpleCustomRenderer.create(mace, new FlywheelMaceRenderer()), mace);
        var grapple = FrogportGrappleItem.FROGPORT_GRAPPLE.get();
        event.registerItem(SimpleCustomRenderer.create(grapple, new FrogportGrappleItemRender()), grapple);
        var incomplete = IncompleteCreatorSwordItems.INCOMPLETE_CREATOR_SWORD.get();
        event.registerItem(SimpleCustomRenderer.create(incomplete, new IncompleteCreatorSwordItemRenderer()), incomplete);
        var incompleteNetherite = IncompleteCreatorSwordItems.INCOMPLETE_NETHERITE_CREATOR_SWORD.get();
        event.registerItem(SimpleCustomRenderer.create(incompleteNetherite, new IncompleteNetheriteCreatorSwordItemRenderer()), incompleteNetherite);

        var fluids = List.of(CSFluids.OMINOUS_ESSENCE, CSFluids.OMINOUS_ESSENCE_II,
                CSFluids.OMINOUS_ESSENCE_III, CSFluids.OMINOUS_ESSENCE_IV, CSFluids.OMINOUS_ESSENCE_V);
        for (int level = 0; level < fluids.size(); level++) {
            event.registerFluidType(OminousEssenceClientExtensions.create(level), fluids.get(level).get().getFluidType());
        }
    }
}
