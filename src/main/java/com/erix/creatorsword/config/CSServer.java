package com.erix.creatorsword.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CSServer extends ConfigBase {

    public final FrogportGrappleCfg frogportGrapple =
            nested(0, FrogportGrappleCfg::new, Comments.frogport_rules);

    @Override
    public @NotNull String getName() {
        return "server";
    }

    private static class Comments {
        static String frogport_rules =
                """
                Frogport Grapple Rules
                - useCustomRules = false: use built-in defaults
                - useCustomRules = true : read selectors from config lists (level0-3, deny)
                
                Selector syntax:
                  - category:<MobCategory>   e.g. category:MONSTER
                  - #<tag>                  e.g. #minecraft:bosses
                  - entity:<id>             e.g. entity:minecraft:zombie
                
                Empty [] level list means OTHERS at that level, but Deny always overrides.
                """;
    }
}