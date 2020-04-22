package ezonius.unifiedstorage.inventory;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;

public class MergedInventories implements SidedInventory {

    private final ArrayList<EnhBarrelBlockEntity> inventory;
    private final int invSize;
    private final HashMap<Integer, Pair<Integer, Integer>> invSlotMap;

    public MergedInventories(ArrayList<EnhBarrelBlockEntity> collect, int invSize) {
        this.inventory = collect;
        this.invSize = collect.stream().map(EnhBarrelBlockEntity::getInvSize).reduce(Integer::sum).orElse(invSize);
        this.invSlotMap = new HashMap<>();
        int slot = 0;
        for (int i = 0; i < inventory.size(); i++) {
            for (int j = 0; j < inventory.get(i).invSize; j++) {
                invSlotMap.put(slot, new Pair<>(i, j));
                slot++;
            }
        }
    }

    @Override
    public int getInvSize() {
        return this.invSize;
    }

    @Override
    public boolean isInvEmpty() {
        return inventory.stream().allMatch(LootableContainerBlockEntity::isInvEmpty);
    }

    @Override
    public ItemStack getInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).getInvStack(targetSlot.getRight());
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).takeInvStack(targetSlot.getRight(), amount);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).removeInvStack(targetSlot.getRight());
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = invSlotMap.get(slot);
        inventory.get(targetSlot.getLeft()).setInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public int getInvMaxStackAmount() {
        return UnifiedStorage.MAX_STACK_SIZE;
    }

    @Override
    public void markDirty() {
        inventory.forEach(BlockEntity::markDirty);
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public void onInvOpen(PlayerEntity player) {
        //inventory.forEach(entry -> entry.onInvOpen(player));
        inventory.get(0).onInvOpen(player);
    }

    @Override
    public void onInvClose(PlayerEntity player) {
        //inventory.forEach(entry -> entry.onInvClose(player));
        inventory.get(0).onInvClose(player);
    }

    @Override
    public boolean isValidInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).isValidInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public void clear() {
        inventory.forEach(LootableContainerBlockEntity::clear);
    }


    @Override
    public int[] getInvAvailableSlots(Direction side) {
        for (int i = 0; i < this.invSize; i++) {
            if (!this.getInvStack(i).isEmpty())
                return new int[]{i};
        }
        return new int[]{};
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        ItemStack targetStack = this.getInvStack(slot);
        return stack.isEmpty() ||
                (targetStack.getItem() == stack.getItem() &&
                        targetStack.getCount() + stack.getCount() <= this.getInvMaxStackAmount());
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return !this.getInvStack(slot).isEmpty();
    }
}
