package com.erix.creatorsword.item.capture_box;

import com.erix.creatorsword.CreatorSword;
import com.erix.creatorsword.client.tooltip.CaptureBoxTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CaptureBoxItem extends Item {
    private static final String KEY_HAS_ENTITY  = "HasCapturedEntity";
    private static final String KEY_ENTITY_TYPE = "CapturedEntityType";
    private static final String KEY_ENTITY_NBT  = "CapturedEntityNbt";

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreatorSword.MODID);
    public static final DeferredHolder<Item, CaptureBoxItem> CAPTURE_BOX =
            ITEMS.register("capture_box",
                    () -> new CaptureBoxItem(new Item.Properties().stacksTo(1)));

    public CaptureBoxItem(Properties properties) {
        super(properties);
    }

    private static CompoundTag getRootTag(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    private static boolean hasFlag(CompoundTag tag) {
        return tag.getBoolean(KEY_HAS_ENTITY);
    }

    public static boolean hasEntity(ItemStack stack) {
        return hasFlag(getRootTag(stack));
    }

    public static boolean hasCapturedMob(ItemStack stack) {
        return getStoredEntityType(stack) != null && getCapturedEntityNbt(stack) != null;
    }

    @Nullable
    public static EntityType<?> getStoredEntityType(ItemStack stack) {
        CompoundTag tag = getRootTag(stack);
        if (!hasFlag(tag))
            return null;

        String typeStr = tag.getString(KEY_ENTITY_TYPE);
        if (typeStr.isEmpty())
            return null;

        ResourceLocation id = ResourceLocation.tryParse(typeStr);
        if (id == null)
            return null;

        return BuiltInRegistries.ENTITY_TYPE.get(id);
    }

    @Nullable
    public static CompoundTag getCapturedEntityNbt(ItemStack stack) {
        CompoundTag tag = getRootTag(stack);
        if (!hasFlag(tag))
            return null;

        CompoundTag entityNbt = tag.getCompound(KEY_ENTITY_NBT);
        return entityNbt.isEmpty() ? null : entityNbt;
    }

    public static boolean captureEntity(ItemStack stack, LivingEntity target) {
        if (hasEntity(stack))
            return false;

        Level level = target.level();
        if (level.isClientSide)
            return false;

        ResourceLocation typeId = EntityType.getKey(target.getType());

        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;
        target.fallDistance = 0.0f;

        CompoundTag entityNbt = new CompoundTag();
        target.saveWithoutId(entityNbt);

        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            t.putBoolean(KEY_HAS_ENTITY, true);
            t.putString(KEY_ENTITY_TYPE, typeId.toString());
            t.put(KEY_ENTITY_NBT, entityNbt);
        });

        target.discard();
        return true;
    }

    public static boolean releaseEntity(Level level, Player player, ItemStack stack, BlockHitResult hit) {
        if (level.isClientSide)
            return false;

        EntityType<?> type = getStoredEntityType(stack);
        CompoundTag entityNbt = getCapturedEntityNbt(stack);
        if (type == null || entityNbt == null)
            return false;

        BlockPos pos = hit.getBlockPos();
        Vec3 spawnPos = Vec3.atCenterOf(pos).add(0, 0.5, 0);

        Entity entity = type.create(level);
        if (!(entity instanceof LivingEntity living))
            return false;

        living.load(entityNbt);
        living.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), player.getXRot());
        level.addFreshEntity(living);

        updateDataComponents(stack, KEY_HAS_ENTITY, KEY_ENTITY_TYPE, KEY_ENTITY_NBT);
        return true;
    }

    public static void updateDataComponents(ItemStack stack, String keyHasEntity, String keyEntityType, String keyEntityNbt) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
            t.remove(keyHasEntity);
            t.remove(keyEntityType);
            t.remove(keyEntityNbt);
        });

        CustomData data2 = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data2.copyTag().isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!hasEntity(stack))
            return InteractionResultHolder.pass(stack);

        double reach = player.blockInteractionRange();
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = eye.add(look.scale(reach));

        net.minecraft.world.level.ClipContext ctx = new net.minecraft.world.level.ClipContext(
                eye,
                end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player
        );
        net.minecraft.world.phys.BlockHitResult hit = level.clip(ctx);

        if (hit.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(stack);

        if (!level.isClientSide) {
            if (!releaseEntity(level, player, stack, hit))
                return InteractionResultHolder.pass(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return hasEntity(stack) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        CompoundTag entityNbt = getCapturedEntityNbt(stack);
        if (entityNbt == null) {
            tooltip.add(Component.translatable("item.creatorsword.capture_box.empty"));
            return;
        }

        EntityType<?> type = getStoredEntityType(stack);
        if (type == null) {
            tooltip.add(Component.translatable("item.creatorsword.capture_box.filled.unknown"));
            return;
        }

        Component mobName = type.getDescription();
        Component customName = parseCustomNameFromEntityNbt(entityNbt, context);

        Component displayName = (customName == null)
                ? mobName
                : Component.empty()
                .append(mobName)
                .append(Component.literal("("))
                .append(customName)
                .append(Component.literal(")"));

        tooltip.add(Component.translatable("item.creatorsword.capture_box.filled", displayName));

        if (entityNbt.contains("Health")) {
            float health = entityNbt.getFloat("Health");
            tooltip.add(Component.translatable(
                    "tooltip.creatorsword.capture_box.health",
                    String.format("%.1f", health)
            ));
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        if (!hasEntity(stack))
            return Optional.empty();

        CompoundTag entityNbt = getCapturedEntityNbt(stack);
        EntityType<?> type = getStoredEntityType(stack);

        if (entityNbt == null || type == null)
            return Optional.empty();

        ResourceLocation id = EntityType.getKey(type);

        return Optional.of(new CaptureBoxTooltip(id.toString(), entityNbt));
    }

    @Nullable
    private static Component parseCustomNameFromEntityNbt(CompoundTag entityNbt, Item.TooltipContext context) {
        if (!entityNbt.contains("CustomName", 8)) return null;

        String json = entityNbt.getString("CustomName");
        if (json.isEmpty()) return null;

        try {
            return Component.Serializer.fromJsonLenient(json, Objects.requireNonNull(context.registries()));
        } catch (Exception ignored) {
            return null;
        }
    }
}
