package com.erix.creatorsword.client;

import com.erix.creatorsword.CreatorSword;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CreatorSword.MODID, bus = EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    public static final KeyMapping TAB_KEY = new KeyMapping(
            "key.creatorsword.charge",              // 翻译键
            InputConstants.Type.KEYSYM,             // 键盘
            GLFW.GLFW_KEY_TAB,                      // Tab 键码
            "key.categories.creatorsword"           // 控件菜单分类
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent ev) {
        ev.register(TAB_KEY);                   // 注册 KeyMapping :contentReference[oaicite:0]{index=0}
    }
}
