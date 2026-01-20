package com.erix.creatorsword.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TravelingFrogTrigger extends SimpleCriterionTrigger<TravelingFrogTrigger.Instance> {

    public TravelingFrogTrigger() {}

    @Override
    public @NotNull Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public record Instance(Optional<ContextAwarePredicate> player) implements SimpleInstance {

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
        return CreatorSwordCriteriaTriggers.TRAVELING_FROG.get()
                .createCriterion(new Instance(Optional.empty()));
    }
}
