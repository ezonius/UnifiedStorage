package ezonius.unifiedstorage.inventory;

import ezonius.unifiedstorage.UnifiedStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface MergedInventoriesInterface extends SidedInventory, Nameable {

    public default void UpdateInventories() {
        setInventories(calcInventories());
        setInvSize(calcInvSize());
        setInvSlotMap(calcInvSlotMap());
    }

    @Override
    public int getInvSize();
    public void setInvSize(int invSize);
    public default int calcInvSize() {
        return getInventories().stream()
                .map(LootableContainerBlockEntity::getInvSize)
                .reduce(Integer::sum)
                .orElse(this.getInvSize());
    }

    public ArrayList<LootableContainerBlockEntity> getInventories();
    public void setInventories(ArrayList<LootableContainerBlockEntity> inventories);
    public default ArrayList<LootableContainerBlockEntity> calcInventories() {
        return getRecursiveAdjacentEntities(new HashSet<>())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public HashMap<Integer, Pair<Integer, Integer>> getInvSlotMap();
    public void setInvSlotMap(HashMap<Integer, Pair<Integer, Integer>> invSlotMap);
    public default HashMap<Integer, Pair<Integer, Integer>> calcInvSlotMap() {
        HashMap<Integer, Pair<Integer, Integer>> invSlotMap = new HashMap<>();
        int slot = 0;
        for (int i = 0; i < getInventories().size(); i++) {
            for (int j = 0; j < getInventories().get(i).getInvSize(); j++) {
                getInvSlotMap().put(slot, new Pair<>(i, j));
                slot++;
            }
        }
        return invSlotMap;
    }

    public Stream<LootableContainerBlockEntity> getRecursiveAdjacentEntities(HashSet<LootableContainerBlockEntity> checkList);

    @Override
    public default boolean isInvEmpty() {
        return getInventories().stream().allMatch(LootableContainerBlockEntity::isInvEmpty);
    }

    @Override
    public default ItemStack getInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).getInvStack(targetSlot.getRight());
    }

    @Override
    public default ItemStack takeInvStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).takeInvStack(targetSlot.getRight(), amount);
    }

    @Override
    public default ItemStack removeInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeInvStack(targetSlot.getRight());
    }

    @Override
    public default void setInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        getInventories().get(targetSlot.getLeft()).setInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public default int getInvMaxStackAmount() {
        return UnifiedStorage.MAX_STACK_SIZE;
    }

    @Override
    public default void markDirty() {
        getInventories().forEach(LootableContainerBlockEntity::markDirty);
    }

    @Override
    public default boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public default void onInvOpen(PlayerEntity player) {
        getInventories().get(0).onInvOpen(player);
    }

    @Override
    public default void onInvClose(PlayerEntity player) {
        getInventories().get(0).onInvClose(player);
    }

    @Override
    public default boolean isValidInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).isValidInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public default void clear() {
        getInventories().forEach(LootableContainerBlockEntity::clear);
    }


    @Override
    public default int[] getInvAvailableSlots(Direction side) {
        return IntStream.range(0, getInvSize()).toArray();
    }

    @Override
    public default boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        ItemStack targetStack = getInvStack(slot);
        return stack.isEmpty() ||
                (targetStack.getItem() == stack.getItem() &&
                        targetStack.getCount() + stack.getCount() <= this.getInvMaxStackAmount());
    }

    @Override
    public default boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return !getInvStack(slot).isEmpty();
    }
}
