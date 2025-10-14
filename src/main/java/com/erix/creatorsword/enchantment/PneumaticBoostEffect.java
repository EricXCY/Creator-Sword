package com.erix.creatorsword.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PneumaticBoostEffect(float base, float perLevel) {
    public static final Codec<PneumaticBoostEffect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("base").forGetter(PneumaticBoostEffect::base),
                    Codec.FLOAT.fieldOf("per_level").forGetter(PneumaticBoostEffect::perLevel)
            ).apply(instance, PneumaticBoostEffect::new)
    );

    public Float applyBoost(float defaultFactor, int level) {
        return defaultFactor * (base + perLevel * (level - 1));
    }
}
