package ezonius.unifiedstorage.inventory;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
import ezonius.unifiedstorage.misc.Utils;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface IMergedInventory extends SidedInventory, Nameable {

    HashSet<LootableContainerBlockEntity> asSingletonHashSet();

    default void UpdateInventories(World world, BlockPos pos) {
        setInventories(calcInventories(world, pos));
        setInvSize(calcInvSize());
        setInvSlotMap(calcInvSlotMap());
    }

    @Override
    int size();
    void setInvSize(int invSize);
    default int calcInvSize() {
        return getInventories().stream()
                .map(LootableContainerBlockEntity::size)
                .reduce(Integer::sum)
                .orElse(0);
    }

    ArrayList<LootableContainerBlockEntity> getInventories();
    void setInventories(ArrayList<LootableContainerBlockEntity> inventories);
    default ArrayList<LootableContainerBlockEntity> calcInventories(World world, BlockPos pos) {
        return Utils.getRecursiveAdjacentEntities(asSingletonHashSet(), world, pos)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    HashMap<Integer, Pair<Integer, Integer>> getInvSlotMap();
    void setInvSlotMap(HashMap<Integer, Pair<Integer, Integer>> invSlotMap);
    default HashMap<Integer, Pair<Integer, Integer>> calcInvSlotMap() {
        HashMap<Integer, Pair<Integer, Integer>> invSlotMap = new HashMap<>();
        int slot = 0;
        for (int i = 0; i < getInventories().size(); i++) {
            for (int j = 0; j < getInventories().get(i).size(); j++) {
                invSlotMap.put(slot, new Pair<>(i, j));
                slot++;
            }
        }
        return invSlotMap;
    }

    @Override
    default boolean isEmpty() {
        return getInventories().stream().allMatch(LootableContainerBlockEntity::isEmpty);
    }

    @Override
    default ItemStack getStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).getStack(targetSlot.getRight());
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeStack(targetSlot.getRight(), amount);
    }

    @Override
    default ItemStack removeStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeStack(targetSlot.getRight());
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        getInventories().get(targetSlot.getLeft()).setStack(targetSlot.getRight(), stack);
    }

    @Override
    default int getMaxCountPerStack() {
        return UnifiedStorage.MAX_STACK_SIZE;
    }

    @Override
    default void markDirty() {
        getInventories().forEach(LootableContainerBlockEntity::markDirty);
    }

    @Override
    default void onOpen(PlayerEntity player) {
        getInventories().get(0).onOpen(player);
    }

    @Override
    default void onClose(PlayerEntity player) {
        getInventories().get(0).onClose(player);
    }

    @Override
    default boolean isValid(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).isValid(targetSlot.getRight(), stack);
    }

    @Override
    default void clear() {
        getInventories().forEach(LootableContainerBlockEntity::clear);
    }

    @Override
    default int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, size()).toArray();
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, Direction dir) {
        ItemStack targetStack = getStack(slot);

        return targetStack.isEmpty() ||
                (targetStack.getItem() == stack.getItem() &&
                        targetStack.getCount() + stack.getCount() <= this.getMaxCountPerStack());
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !getStack(slot).isEmpty();
    }
}
