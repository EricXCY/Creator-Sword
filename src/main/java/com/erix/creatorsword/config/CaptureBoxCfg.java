package com.erix.creatorsword.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CaptureBoxCfg extends ConfigBase {
    public final ConfigInt lootRolls = i(3, 1, "lootRolls",
            "Independent entity loot table rolls when crushing a filled Capture Box.",
            "This rolls the table repeatedly, rather than multiplying a single result.");

    @Override
    public @NotNull String getName() {
        return "captureBox";
    }
}
