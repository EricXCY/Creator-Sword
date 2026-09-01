package com.erix.creatorsword.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FrogportGrappleCfg extends ConfigBase {

    public enum RuleMode {
        DEFAULT,
        CUSTOM
    }

    public final ConfigEnum<RuleMode> ruleMode =
            e(RuleMode.DEFAULT, "captureRuleMode", Comments.ruleMode);

    public final ConfigBool allowTargetPlayers = b(
            true,
            "allowTargetPlayers",
            Comments.allowTargetPlayers
    );

    public final ConfigInt tongueLength = i(48, 1, "tongueLength", Comments.tongueLength);

    private ModConfigSpec.ConfigValue<Double> physicalStructurePullStrength;
    private ModConfigSpec.ConfigValue<Double> physicalStructurePullMaxSpeed;

    private ModConfigSpec.ConfigValue<List<? extends String>> level0;
    private ModConfigSpec.ConfigValue<List<? extends String>> level1;
    private ModConfigSpec.ConfigValue<List<? extends String>> level2;
    private ModConfigSpec.ConfigValue<List<? extends String>> level3;
    private ModConfigSpec.ConfigValue<List<? extends String>> deny;

    @Override
    public void registerAll(ModConfigSpec.@NotNull Builder b) {
        super.registerAll(b);

        physicalStructurePullStrength = b
                .comment(Comments.physicalStructurePullStrength)
                .define("physicalStructurePullStrength", 5.0D,
                        value -> value instanceof Number number
                                && Double.isFinite(number.doubleValue())
                                && number.doubleValue() >= 0.0D);
        physicalStructurePullMaxSpeed = b
                .comment(Comments.physicalStructurePullMaxSpeed)
                .define("physicalStructurePullMaxSpeed", 5.0D,
                        value -> value instanceof Number number
                                && Double.isFinite(number.doubleValue())
                                && number.doubleValue() >= 0.0D);

        b.push("custom_rule_lists");
        b.comment("Advanced: edit rules here (GUI may not support list editing well).");

        level0 = b.defineListAllowEmpty("level0",
                List.of(
                        "category:CREATURE",
                        "category:AMBIENT",
                        "category:WATER_CREATURE",
                        "category:WATER_AMBIENT",
                        "category:UNDERGROUND_WATER_CREATURE",
                        "category:AXOLOTLS"
                ),
                () -> "category:CREATURE",
                o -> o instanceof String);

        level1 = b.defineListAllowEmpty("level1",
                List.of(), // empty => OTHERS
                () -> "category:CREATURE",
                o -> o instanceof String);

        level2 = b.defineListAllowEmpty("level2",
                List.of("category:MONSTER"),
                () -> "category:MONSTER",
                o -> o instanceof String);

        level3 = b.defineListAllowEmpty("level3",
                List.of(
                        "entity:minecraft:ender_dragon",
                        "entity:minecraft:wither",
                        "entity:minecraft:warden"
                ),
                () -> "entity:minecraft:warden",
                o -> o instanceof String);

        deny = b.defineListAllowEmpty("deny",
                List.of(),
                () -> "#minecraft:bosses",
                o -> o instanceof String);

        b.pop(); // <- 结束分组
    }

    private volatile FrogportGrappleRules bakedCustom;
    private volatile int bakedCustomFingerprint = 0;

    public FrogportGrappleRules customRules(RegistryAccess ra) {
        int fp = fingerprintCustom();
        FrogportGrappleRules local = bakedCustom;
        if (local != null && fp == bakedCustomFingerprint)
            return local;

        synchronized (this) {
            fp = fingerprintCustom();
            if (bakedCustom != null && fp == bakedCustomFingerprint)
                return bakedCustom;

            bakedCustom = FrogportGrappleRules.fromConfig(
                    ra,
                    level0.get(),
                    level1.get(),
                    level2.get(),
                    level3.get(),
                    deny.get()
            );
            bakedCustomFingerprint = fp;
            return bakedCustom;
        }
    }

    private int fingerprintCustom() {
        return Objects.hash(
                level0.get(), level1.get(), level2.get(), level3.get(), deny.get()
        );
    }

    public double physicalStructurePullStrength() {
        return physicalStructurePullStrength.get();
    }

    public double physicalStructurePullMaxSpeed() {
        return physicalStructurePullMaxSpeed.get();
    }

    @Override
    public @NotNull String getName() { return "Frogport Grapple"; }

    private static class Comments {
        static String ruleMode = "Rule mode: DEFAULT uses built-in rules; CUSTOM uses config selector lists.";
        static String allowTargetPlayers = "Allow Frogport Grapple to target and pull player entities.";
        static String tongueLength = "Max tongue reach distance (blocks).";
        static String physicalStructurePullStrength =
                "Impulse applied each tick when a grounded player holds Shift while hooked to a physical structure.";
        static String physicalStructurePullMaxSpeed =
                "Maximum structure velocity toward the player caused by the grapple.";
    }
}
