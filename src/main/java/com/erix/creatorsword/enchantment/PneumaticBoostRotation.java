package com.erix.creatorsword.enchantment;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.datagen.enchantments.EnchantmentKeys;
import com.erix.creatorsword.item.creator_sword.BaseCreatorSwordItem;
import com.erix.creatorsword.item.flywheel_mace.FlywheelMaceItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = CreatorSword.MODID, value = Dist.CLIENT)
public final class PneumaticBoostRotation {
    private static final Map<ItemStack, Rotation> ROTATIONS = new WeakHashMap<>();

    private PneumaticBoostRotation() {}

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            ROTATIONS.clear();
            return;
        }
        if (minecraft.isPaused()) return;

        ItemStack held = minecraft.player.getMainHandItem();
        float bonus = 0;
        if (minecraft.player.getAttackStrengthScale(0) < 1) {
            float perLevel = held.getItem() instanceof BaseCreatorSwordItem ? 2f
                    : held.getItem() instanceof FlywheelMaceItem ? 5f : 0f;
            if (perLevel > 0) {
                bonus = perLevel * EnchantmentKeys.getEnchantmentLevel(
                        minecraft.level.registryAccess(), EnchantmentKeys.PNEUMATIC_BOOST, held);
                ROTATIONS.computeIfAbsent(held, ignored -> new Rotation());
            }
        }
        for (var entry : ROTATIONS.entrySet()) {
            Rotation rotation = entry.getValue();
            rotation.previous = rotation.angle % 360f;
            rotation.angle = rotation.previous + (entry.getKey() == held ? bonus : 0);
        }
    }

    public static float getExtraAngle(ItemStack stack, float partialTicks) {
        Rotation rotation = ROTATIONS.get(stack);
        if (rotation == null) return 0;
        return rotation.previous + (rotation.angle - rotation.previous) * partialTicks;
    }

    private static final class Rotation {
        private float previous;
        private float angle;
    }
}
