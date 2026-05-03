package com.erix.creatorsword.data;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.config.CreatorSwordConfigs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CSConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, CreatorSword.MODID);

    public static final Supplier<MapCodec<ConfigEnabledCondition>> CONFIG_ENABLED =
            CONDITIONS.register("config_enabled", () -> ConfigEnabledCondition.CODEC);

    public static void register(IEventBus modEventBus) {
        CONDITIONS.register(modEventBus);
    }

    public record ConfigEnabledCondition(String key) implements ICondition {
        public static final MapCodec<ConfigEnabledCondition> CODEC =
                Codec.STRING
                        .fieldOf("key")
                        .xmap(ConfigEnabledCondition::new, ConfigEnabledCondition::key);

        @Override
        public boolean test(@NotNull IContext context) {
            if (CreatorSwordConfigs.server() == null)
                return true;

            try {
                return CreatorSwordConfigs.common().recipes.isEnabled(key);
            } catch (IllegalStateException e) {
                return true;
            }
        }

        @Override
        public @NotNull MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
    }
}