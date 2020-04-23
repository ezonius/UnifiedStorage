package ezonius.unifiedstorage.inventory;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
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

public interface MergedInventoriesInterface extends SidedInventory, Nameable {

    HashSet<LootableContainerBlockEntity> asSingletonHashSet();

    default void UpdateInventories(World world, BlockPos pos) {
        setInventories(calcInventories(world, pos));
        setInvSize(calcInvSize());
        setInvSlotMap(calcInvSlotMap());
    }

    @Override
    int getInvSize();
    void setInvSize(int invSize);
    default int calcInvSize() {
        return getInventories().stream()
                .map(LootableContainerBlockEntity::getInvSize)
                .reduce(Integer::sum)
                .orElse(0);
    }

    ArrayList<LootableContainerBlockEntity> getInventories();
    void setInventories(ArrayList<LootableContainerBlockEntity> inventories);
    default ArrayList<LootableContainerBlockEntity> calcInventories(World world, BlockPos pos) {
        return getRecursiveAdjacentEntities(asSingletonHashSet(), world, pos)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    HashMap<Integer, Pair<Integer, Integer>> getInvSlotMap();
    void setInvSlotMap(HashMap<Integer, Pair<Integer, Integer>> invSlotMap);
    default HashMap<Integer, Pair<Integer, Integer>> calcInvSlotMap() {
        HashMap<Integer, Pair<Integer, Integer>> invSlotMap = new HashMap<>();
        int slot = 0;
        for (int i = 0; i < getInventories().size(); i++) {
            for (int j = 0; j < getInventories().get(i).getInvSize(); j++) {
                invSlotMap.put(slot, new Pair<>(i, j));
                slot++;
            }
        }
        return invSlotMap;
    }

    Stream<LootableContainerBlockEntity> getRecursiveAdjacentEntities(HashSet<LootableContainerBlockEntity> checkList, World world, BlockPos pos);

    @Override
    default boolean isInvEmpty() {
        return getInventories().stream().allMatch(LootableContainerBlockEntity::isInvEmpty);
    }

    @Override
    default ItemStack getInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).getInvStack(targetSlot.getRight());
    }

    @Override
    default ItemStack takeInvStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).takeInvStack(targetSlot.getRight(), amount);
    }

    @Override
    default ItemStack removeInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeInvStack(targetSlot.getRight());
    }

    @Override
    default void setInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        getInventories().get(targetSlot.getLeft()).setInvStack(targetSlot.getRight(), stack);
    }

    @Override
    default int getInvMaxStackAmount() {
        return UnifiedStorage.MAX_STACK_SIZE;
    }

    @Override
    default void markDirty() {
        getInventories().forEach(LootableContainerBlockEntity::markDirty);
    }

    @Override
    default void onInvOpen(PlayerEntity player) {
        getInventories().get(0).onInvOpen(player);
    }

    @Override
    default void onInvClose(PlayerEntity player) {
        getInventories().get(0).onInvClose(player);
    }

    @Override
    default boolean isValidInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).isValidInvStack(targetSlot.getRight(), stack);
    }

    @Override
    default void clear() {
        getInventories().forEach(LootableContainerBlockEntity::clear);
    }

    @Override
    default int[] getInvAvailableSlots(Direction side) {
        return IntStream.range(0, getInvSize()).toArray();
    }

    @Override
    default boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        ItemStack targetStack = getInvStack(slot);
        return targetStack.isEmpty() ||
                (targetStack.getItem() == stack.getItem() &&
                        targetStack.getCount() + stack.getCount() <= this.getInvMaxStackAmount());
    }

    @Override
    default boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return !getInvStack(slot).isEmpty();
    }
}
