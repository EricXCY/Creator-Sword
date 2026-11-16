package com.erix.creatorsword.item.processing;

import com.erix.creatorsword.item.creator_sword.CNYCreatorSwordItem;
import com.erix.creatorsword.item.creator_sword.TrialCreatorSwordItem;
import com.erix.creatorsword.item.creator_sword.TidecrestCreatorSwordItem;
import com.erix.creatorsword.item.flywheel_mace.FlywheelMaceItem;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import java.util.Optional;

public enum ProcessingKind {
    PRESS(SoundEvents.ANVIL_LAND), SMOKE(SoundEvents.FIRE_EXTINGUISH),
    POLISH(SoundEvents.GRINDSTONE_USE), WASH(SoundEvents.GENERIC_SPLASH);

    public final SoundEvent sound;

    ProcessingKind(SoundEvent sound) { this.sound = sound; }

    public boolean usesAttack() { return this == PRESS; }

    public static ProcessingKind of(ItemStack tool) {
        if (tool.getItem() instanceof FlywheelMaceItem) return ProcessingKind.PRESS;
        if (tool.getItem() instanceof CNYCreatorSwordItem) return ProcessingKind.SMOKE;
        if (tool.getItem() instanceof TrialCreatorSwordItem) return ProcessingKind.POLISH;
        if (tool.getItem() instanceof TidecrestCreatorSwordItem) return ProcessingKind.WASH;
        return null;
    }

    public Optional<? extends RecipeHolder<?>> findRecipe(Player player, ItemStack stack) {
        var input = new SingleRecipeInput(stack);
        return switch (this) {
            case PRESS -> pressingRecipe(player, stack);
            case SMOKE -> player.level().getRecipeManager().getRecipeFor(RecipeType.SMOKING, input, player.level());
            case POLISH -> AllRecipeTypes.SANDPAPER_POLISHING.find(input, player.level());
            case WASH -> AllRecipeTypes.SPLASHING.find(input, player.level());
        };
    }

    private static Optional<RecipeHolder<PressingRecipe>> pressingRecipe(Player player, ItemStack stack) {
        var assembly = SequencedAssemblyRecipe.getRecipe(player.level(), stack,
                AllRecipeTypes.PRESSING.getType(), PressingRecipe.class);
        return assembly.isPresent() ? assembly : AllRecipeTypes.PRESSING.find(new SingleRecipeInput(stack), player.level());
    }

}
