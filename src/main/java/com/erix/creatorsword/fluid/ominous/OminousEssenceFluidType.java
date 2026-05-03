package com.erix.creatorsword.fluid.ominous;

import com.erix.creatorsword.CreatorSword;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class OminousEssenceFluidType extends FluidType {
    private final int amplifier;

    public OminousEssenceFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int amplifier) {
        super(properties);
        this.amplifier = Math.clamp(amplifier, 0, 4);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull FluidStack stack) {
        return switch (amplifier) {
            case 1 -> "fluid." + CreatorSword.MODID + ".ominous_essence_ii";
            case 2 -> "fluid." + CreatorSword.MODID + ".ominous_essence_iii";
            case 3 -> "fluid." + CreatorSword.MODID + ".ominous_essence_iv";
            case 4 -> "fluid." + CreatorSword.MODID + ".ominous_essence_v";
            default -> "fluid." + CreatorSword.MODID + ".ominous_essence";
        };
    }

    @Override
    public @NotNull Component getDescription(@NotNull FluidStack stack) {
        return Component.translatable(getDescriptionId(stack));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath("create", "fluid/potion_still");
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath("create", "fluid/potion_flow");
            }

            @Override
            public int getTintColor(@NotNull FluidStack stack) {
                return getColorByAmplifier(amplifier);
            }

            @Override
            public int getTintColor(@NotNull FluidState state, @NotNull BlockAndTintGetter getter, @NotNull BlockPos pos) {
                return getColorByAmplifier(amplifier);
            }

            @Override
            public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount,
                                                    @NotNull Vector3f fluidFogColor) {
                return switch (amplifier) {
                    case 1 -> new Vector3f(0.29F, 0.11F, 0.41F);
                    case 2 -> new Vector3f(0.23F, 0.09F, 0.32F);
                    case 3 -> new Vector3f(0.17F, 0.07F, 0.24F);
                    case 4 -> new Vector3f(0.11F, 0.04F, 0.16F);
                    default -> new Vector3f(0.35F, 0.05F, 0.45F);
                };
            }
        });
    }

    private static int getColorByAmplifier(int amplifier) {
        return switch (Math.clamp(amplifier, 0, 4)) {
            case 1 -> 0xFF4A1D68; // II
            case 2 -> 0xFF3A1752; // III
            case 3 -> 0xFF2B113D; // IV
            case 4 -> 0xFF1C0B29; // V
            default -> 0xFF5A247D; // I
        };
    }
}