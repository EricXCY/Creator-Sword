package com.erix.creatorsword.block.sword_pedestal;

import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.neoforged.neoforge.items.IItemHandler;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;

public class SwordPedestalBlockEntity extends GeneratingKineticBlockEntity {
    public static final float RPM = 64;
    private ScrollOptionBehaviour<RotationDirection> movementDirection;
    private ScrollValueBehaviour targetSpeed;
    private ItemStack heldItem = ItemStack.EMPTY;

    private final IItemHandler itemHandler = new IItemHandler() {
        private void checkSlot(int slot) {
            if (slot != 0) throw new IndexOutOfBoundsException("Invalid pedestal slot: " + slot);
        }
        @Override public int getSlots() { return 1; }
        @Override public ItemStack getStackInSlot(int slot) {
            checkSlot(slot);
            return getHeldItem();
        }
        @Override public int getSlotLimit(int slot) {
            checkSlot(slot);
            return 1;
        }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            checkSlot(slot);
            return !stack.isEmpty() && capacityFor(stack) > 0;
        }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            checkSlot(slot);
            if (!isItemValid(slot, stack) || hasHeldItem() || level == null || isRemoved()) return stack;
            if (!simulate && (level.isClientSide || !SwordPedestalBlockEntity.this.insertItem(stack))) return stack;
            return stack.copyWithCount(stack.getCount() - 1);
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            checkSlot(slot);
            if (amount <= 0 || !hasHeldItem() || level == null || isRemoved()) return ItemStack.EMPTY;
            return simulate ? getHeldItem() : extract();
        }
    };
    public IItemHandler getItemHandler() { return itemHandler; }

    public SwordPedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    public boolean hasHeldItem() { return !heldItem.isEmpty(); }
    private int capacityFor(ItemStack stack) {
        return getBlockState().getBlock() instanceof SwordPedestalBlock pedestal
                ? pedestal.getCapacityFor(stack) : 0;
    }
    public int getProvidedCapacity() { return capacityFor(heldItem); }
    public ItemStack getHeldItem() { return heldItem.copy(); }
    @Override public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        if (getBlockState().getBlock() instanceof SwordPedestalBlock pedestal && pedestal.hasSpeedControl()) {
            int max = AllConfigs.server().kinetics.maxRotationSpeed.get();
            targetSpeed = new KineticScrollValueBehaviour(
                    CreateLang.translateDirect("kinetics.speed_controller.rotation_speed"), this, new SwordPedestalDirectionSlot());
            targetSpeed.between(-max, max);
            targetSpeed.value = Math.min((int) RPM, max);
            targetSpeed.withCallback(value -> updateGeneratedRotation());
            behaviours.add(targetSpeed);
            return;
        }
        movementDirection = new ScrollOptionBehaviour<>(RotationDirection.class,
                CreateLang.translateDirect("contraptions.windmill.rotation_direction"), this, new SwordPedestalDirectionSlot());
        movementDirection.onlyActiveWhen(() -> !hasSource());
        movementDirection.withCallback(value -> {
            if (level == null || level.isClientSide) return;
            if (hasSource() && getTheoreticalSpeed() != 0) syncDirectionWithNetwork();
            else updateGeneratedRotation();
        });
        behaviours.add(movementDirection);
    }
    private void syncDirectionWithNetwork() {
        if (movementDirection == null) return;
        if (getTheoreticalSpeed() != 0)
            movementDirection.setValue(getTheoreticalSpeed() < 0 ? 1 : 0);
    }
    @Override public void tick() {
        if (level != null && !level.isClientSide && hasSource()) syncDirectionWithNetwork();
        super.tick();
    }
    public boolean insert(Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!heldItem.isEmpty() || capacityFor(stack) == 0) return false;
        if (level == null) return false;
        if (level.isClientSide) return true;
        if (!insertItem(stack)) return false;
        if (!player.isCreative()) stack.shrink(1);
        return true;
    }
    private boolean insertItem(ItemStack stack) {
        if (level == null || level.isClientSide || hasHeldItem() || stack.isEmpty() || capacityFor(stack) <= 0)
            return false;
        syncDirectionWithNetwork();
        if (targetSpeed != null && getTheoreticalSpeed() != 0)
            targetSpeed.setValue((int) Math.copySign(Math.abs(targetSpeed.getValue()), getTheoreticalSpeed()));
        heldItem = stack.copyWithCount(1);
        changed();
        return true;
    }
    public ItemStack extract() {
        if (level == null || level.isClientSide || heldItem.isEmpty()) return ItemStack.EMPTY;
        var result = takeForRemoval();
        changed();
        return result;
    }
    private ItemStack takeForRemoval() {
        var result = heldItem;
        heldItem = ItemStack.EMPTY;
        return result;
    }
    private void changed() {
        setChanged();
        updateGeneratedRotation();
        sendData();
        if (level != null) level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
    @Override public void destroy() {
        super.destroy();
        if (level != null && !level.isClientSide) {
            Block.popResource(level, worldPosition, takeForRemoval());
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }
    @Override protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1.5);
    }
    @Override public void initialize() {
        super.initialize();
        if (level != null && !level.isClientSide) updateGeneratedRotation();
    }
    @Override public float getGeneratedSpeed() {
        if (getProvidedCapacity() == 0) return 0;
        return targetSpeed != null ? targetSpeed.getValue()
                : RPM * (movementDirection.get() == RotationDirection.CLOCKWISE ? 1 : -1);
    }
    @Override public float calculateAddedStressCapacity() {
        float rpm = targetSpeed != null ? Math.abs(targetSpeed.getValue()) : RPM;
        return lastCapacityProvided = rpm == 0 ? 0 : getProvidedCapacity() / rpm;
    }
    @Override public float calculateStressApplied() { return lastStressApplied = 0; }
    @Override protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (movementDirection != null) tag.putInt("RotationDirection", movementDirection.getValue() == 0 ? 1 : -1);
        if (!heldItem.isEmpty()) tag.put("HeldItem", heldItem.save(registries));
    }
    @Override protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (movementDirection != null) {
            int legacyDirection = tag.getInt("RotationDirection");
            movementDirection.value = tag.contains("ScrollValue") ? tag.getInt("ScrollValue") : (legacyDirection < 0 ? 1 : 0);
            movementDirection.value = Math.clamp(movementDirection.value, 0, 1);
        }
        heldItem = ItemStack.parseOptional(registries, tag.getCompound("HeldItem"));
    }
}
