package com.erix.creatorsword.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CSRecipeCfg extends ConfigBase {

    public final ConfigBool enableOminousEssenceRecipes = b(
            true,
            "enableOminousEssenceRecipes",
            Comments.enableOminousEssenceRecipes
    );

    public final ConfigBool enableTrialKeyRecipes = b(
            true,
            "enableTrialKeyRecipes",
            Comments.enableTrialKeyRecipes
    );

    public final ConfigBool enableOminousTrialKeyRecipes = b(
            true,
            "enableOminousTrialKeyRecipes",
            Comments.enableOminousTrialKeyRecipes
    );

    public final ConfigBool enablePrecisionTrialKeyRecipes = b(
            true,
            "enablePrecisionTrialKeyRecipes",
            Comments.enablePrecisionTrialKeyRecipes
    );

    public final ConfigBool enablePrecisionOminousTrialKeyRecipes = b(
            true,
            "enablePrecisionOminousTrialKeyRecipes",
            Comments.enablePrecisionOminousTrialKeyRecipes
    );

    public boolean isEnabled(String key) {
        return switch (key) {
            case "ominous_essence" -> enableOminousEssenceRecipes.get();
            case "trial_key" -> enableTrialKeyRecipes.get();
            case "ominous_trial_key" -> enableOminousTrialKeyRecipes.get();
            case "precision_trial_key" -> enablePrecisionTrialKeyRecipes.get();
            case "precision_ominous_trial_key" -> enablePrecisionOminousTrialKeyRecipes.get();
            default -> false;
        };
    }

    @Override
    public @NotNull String getName() {
        return "recipes";
    }

    private static class Comments {
        static String enableOminousEssenceRecipes =
                "Enable Create mixing recipes for Ominous Essence fluids.";

        static String enableTrialKeyRecipes =
                "Enable Create recipes for crafting Trial Keys.";

        static String enableOminousTrialKeyRecipes =
                "Enable Create recipes for crafting Ominous Trial Keys.";

        static String enablePrecisionTrialKeyRecipes =
                "Enable Create recipes for crafting Precision Trial Keys.";

        static String enablePrecisionOminousTrialKeyRecipes =
                "Enable Create recipes for crafting Precision Ominous Trial Keys.";
    }
}