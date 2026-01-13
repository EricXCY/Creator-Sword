package com.erix.creatorsword.client.tooltip;

import com.erix.creatorsword.item.cogwheel_shield.CogwheelShieldItems;
import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.erix.creatorsword.item.frogport_grapple.FrogportGrappleItem;
import com.erix.creatorsword.item.incomplete_creator_sword.IncompleteCreatorSwordItems;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper.Palette;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT)
public class ClientTooltipRegistration {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerCreateTooltip(CogwheelShieldItems.COGWHEEL_SHIELD.get());
            registerCreateTooltip(CreatorSwordItems.CREATOR_SWORD.get());
            registerCreateTooltip(CreatorSwordItems.NETHERITE_CREATOR_SWORD.get());
            registerCreateTooltip(IncompleteCreatorSwordItems.INCOMPLETE_CREATOR_SWORD.get());
            registerCreateTooltip(IncompleteCreatorSwordItems.INCOMPLETE_NETHERITE_CREATOR_SWORD.get());
            registerCreateTooltip(FrogportGrappleItem.FROGPORT_GRAPPLE.get());
        });
    }

    private static void registerCreateTooltip(net.minecraft.world.item.Item item) {
        TooltipModifier base = new ItemDescription.Modifier(item, Palette.STANDARD_CREATE);
        TooltipModifier.REGISTRY.register(item, base);
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CaptureBoxTooltip.class,
                t -> new CaptureBoxTooltipClient(t.getEntityTypeId(), t.getEntityNbt()));
    }
}
