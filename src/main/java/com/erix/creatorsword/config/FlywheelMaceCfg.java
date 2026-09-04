package com.erix.creatorsword.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class FlywheelMaceCfg extends ConfigBase {
    public final ConfigInt chargeTicks = i(80, 5, "chargeTicks",
            "Ticks required to gain one full unit of flywheel mace charge.");
    public final ConfigInt dashTicks = i(20, 1, "dashTicks",
            "Duration of a flywheel mace dash in ticks.");

    private ModConfigSpec.ConfigValue<Double> baseDashSpeed;
    private ModConfigSpec.ConfigValue<Double> energyDashSpeed;

    @Override
    public void registerAll(ModConfigSpec.@NotNull Builder builder) {
        super.registerAll(builder);
        baseDashSpeed = builder.comment("Base dash speed in blocks per tick.")
                .defineInRange("baseDashSpeed", 0.7D, 0.0D, 100.0D);
        energyDashSpeed = builder.comment("Additional dash speed per unit of stored energy.")
                .defineInRange("energyDashSpeed", 0.9D, 0.0D, 100.0D);
    }

    public double baseDashSpeed() {
        return baseDashSpeed.get();
    }

    public double energyDashSpeed() {
        return energyDashSpeed.get();
    }

    @Override
    public @NotNull String getName() {
        return "flywheelMace";
    }
}
