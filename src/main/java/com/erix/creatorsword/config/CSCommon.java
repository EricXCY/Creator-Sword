package com.erix.creatorsword.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CSCommon extends ConfigBase {

    public final CSRecipeCfg recipes =
            nested(0, CSRecipeCfg::new, Comments.recipes);

    @Override
    public @NotNull String getName() {
        return "common";
    }

    private static class Comments {
        static String recipes =
                """
                Recipe Toggles
                These options control whether Creator Sword's Create processing recipes are loaded.
    
                Changes to these options require re-entering the world or restarting the server
                before recipe visibility and availability are refreshed.
                """;
    }
}