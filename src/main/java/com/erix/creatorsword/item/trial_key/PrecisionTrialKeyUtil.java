package com.erix.creatorsword.item.trial_key;

import com.erix.creatorsword.item.CSItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.vault.VaultConfig;

public final class PrecisionTrialKeyUtil {
    private PrecisionTrialKeyUtil() {
    }

    public static boolean isPrecisionTrialKey(ItemStack stack) {
        return stack.is(CSItems.PRECISION_TRIAL_KEY.get());
    }

    public static boolean isPrecisionOminousTrialKey(ItemStack stack) {
        return stack.is(CSItems.PRECISION_OMINOUS_TRIAL_KEY.get());
    }

    public static boolean isPrecisionKey(ItemStack stack) {
        return isPrecisionTrialKey(stack) || isPrecisionOminousTrialKey(stack);
    }

    public static boolean matchesVaultKey(VaultConfig config, ItemStack stack) {
        ItemStack vaultKey = config.keyItem();

        if (isPrecisionTrialKey(stack)) {
            return vaultKey.is(Items.TRIAL_KEY);
        }

        if (isPrecisionOminousTrialKey(stack)) {
            return vaultKey.is(Items.OMINOUS_TRIAL_KEY);
        }

        return false;
    }
}