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

    /** 在 Mod 初始化时由主类调用，用于注册事件 */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(KeyBindings::onRegisterKeyMappings);
    }

    /**
     * 通过 NeoForge 自定义按键注册事件，把 KeyMapping 放到 Controls 界面
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ROTATE_COGWHEEL);
    }
}
