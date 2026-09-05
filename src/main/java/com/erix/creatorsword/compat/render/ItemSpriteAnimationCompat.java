package com.erix.creatorsword.compat.render;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.function.Consumer;

/** Optional sprite tracking for custom-rendered items, including the foil rendering path. */
public final class ItemSpriteAnimationCompat {
    private static final Consumer<TextureAtlasSprite> MARK_ACTIVE = findMarker();
    private static boolean failed;

    private ItemSpriteAnimationCompat() {}

    public static void markActive(List<BakedQuad> quads) {
        if (MARK_ACTIVE == null || failed) return;
        try {
            Set<TextureAtlasSprite> sprites = new HashSet<>();
            for (BakedQuad quad : quads) {
                TextureAtlasSprite sprite = quad.getSprite();
                if (sprite != null && sprites.add(sprite)) MARK_ACTIVE.accept(sprite);
            }
        } catch (RuntimeException exception) {
            failed = true;
            LogUtils.getLogger().warn("Could not activate item texture animations", exception);
        }
    }

    private static Consumer<TextureAtlasSprite> findMarker() {
        // Resolve once, without making either optimization mod a required dependency.
        for (String name : new String[]{
                "net.caffeinemc.mods.sodium.api.texture.SpriteUtil",
                "org.embeddedt.embeddium.api.render.texture.SpriteUtil"}) {
            try {
                Class<?> api = Class.forName(name);
                Method method = api.getMethod("markSpriteActive", TextureAtlasSprite.class);
                Object receiver = Modifier.isStatic(method.getModifiers()) ? null : api.getField("INSTANCE").get(null);
                return sprite -> {
                    try {
                        method.invoke(receiver, sprite);
                    } catch (ReflectiveOperationException exception) {
                        throw new IllegalStateException("Sprite animation API invocation failed", exception);
                    }
                };
            } catch (ClassNotFoundException ignored) {
            } catch (ReflectiveOperationException | LinkageError exception) {
                LogUtils.getLogger().warn("Could not resolve sprite animation API {}", name, exception);
            }
        }
        return null;
    }
}
