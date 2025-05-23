package com.erix.creatorsword;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class KeyBindings {
    public static final KeyMapping ROTATE_COGWHEEL = new KeyMapping(
            "creatorsword.keybinds.keybind.rotate_cogwheel",
            InputConstants.KEY_V,
            "creatorsword.keybinds.category"
    );

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(KeyBindings::onRegisterKeyMappings);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ROTATE_COGWHEEL);
    }
}
