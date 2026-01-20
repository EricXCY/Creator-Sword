package com.erix.creatorsword.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FullSpeedTrigger extends SimpleCriterionTrigger<FullSpeedTrigger.Instance> {
    public FullSpeedTrigger() {}

    @Override
    public @NotNull Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public static class Instance implements SimpleInstance {
        private final Optional<ContextAwarePredicate> player;

        public Instance(Optional<ContextAwarePredicate> player) {
            this.player = player;
        }

        public static final Codec<Instance> CODEC = ContextAwarePredicate.CODEC
                .optionalFieldOf("player")
                .xmap(Instance::new, i -> i.player)
                .codec();

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return this.player;
        }
    }

    public static Criterion<Instance> criterion() {
        return CreatorSwordCriteriaTriggers.FULL_SPEED.get().createCriterion(new Instance(Optional.empty()));
    }
}
