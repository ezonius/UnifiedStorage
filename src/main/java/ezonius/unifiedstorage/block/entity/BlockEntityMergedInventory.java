package ezonius.unifiedstorage.block.entity;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.inventory.MergedInventoriesInterface;
import net.minecraft.block.entity.*;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class BlockEntityMergedInventory extends LootableContainerBlockEntity implements MergedInventoriesInterface {
    private ArrayList<LootableContainerBlockEntity> inventories;
    private int invSize;
    private HashMap<Integer, Pair<Integer, Integer>> invSlotMap;

    public BlockEntityMergedInventory(BlockEntityType<?> type) {
        super(type);
        UpdateInventories(this.getWorld(), this.getPos());
    }

    @Override
    public void UpdateInventories(World world, BlockPos pos) {
        setInventories(calcInventories(world, pos));
        setInvSize(calcInvSize());
        setInvSlotMap(calcInvSlotMap());
    }

    public Stream<LootableContainerBlockEntity> getAdjacentInventories(World world, BlockPos pos) {
        ArrayList<BlockEntity> adjacentEntities = new ArrayList<>();
        if (world != null) {
            adjacentEntities.add(world.getBlockEntity(pos.down()));
            adjacentEntities.add(world.getBlockEntity(pos.up()));
            adjacentEntities.add(world.getBlockEntity(pos.north()));
            adjacentEntities.add(world.getBlockEntity(pos.south()));
            adjacentEntities.add(world.getBlockEntity(pos.east()));
            adjacentEntities.add(world.getBlockEntity(pos.west()));
        }
        ArrayList<LootableContainerBlockEntity> distinct = adjacentEntities.stream()
                .filter((entry) -> {
                    boolean b = entry instanceof BarrelBlockEntity ||
                            entry instanceof ChestBlockEntity ||
                            entry instanceof ShulkerBoxBlockEntity;
                    return b;
                })
                .map(entry -> ((LootableContainerBlockEntity) entry))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        return distinct.stream();
    }

    @Override
    public Stream<LootableContainerBlockEntity> getRecursiveAdjacentEntities(HashSet<LootableContainerBlockEntity> checkList, World world, BlockPos pos) {
        ArrayList<LootableContainerBlockEntity> filteredAdjacent = getAdjacentInventories(world, pos)
                .filter(entry -> {
                    if (!checkList.contains(entry)) {
                        checkList.add(entry);
                        return true;
                    }
                    return false;
                }).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<LootableContainerBlockEntity> concat = Stream.concat(
                filteredAdjacent.stream(),
                filteredAdjacent.stream()
                        .flatMap(stBlockEntity -> {
                            Stream<LootableContainerBlockEntity> recursiveAdjacentEntities = this.getRecursiveAdjacentEntities(checkList, stBlockEntity.getWorld(), stBlockEntity.getPos());
                            return recursiveAdjacentEntities;
                        })).collect(Collectors.toCollection(ArrayList::new));
        return concat.stream();
    }


    @Override
    public ArrayList<LootableContainerBlockEntity> getInventories() {
        return inventories;
    }

    @Override
    public void setInventories(ArrayList<LootableContainerBlockEntity> inventories) {
        this.inventories = inventories;
    }

    @Override
    public HashMap<Integer, Pair<Integer, Integer>> getInvSlotMap() {
        return invSlotMap;
    }

    @Override
    public void setInvSlotMap(HashMap<Integer, Pair<Integer, Integer>> invSlotMap) {
        this.invSlotMap = invSlotMap;
    }


    @Override
    public int getInvSize() {
        return invSize;
    }

    @Override
    public void setInvSize(int invSize) {
        this.invSize = invSize;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        DefaultedList<ItemStack> ret = DefaultedList.of();
        for (int i = 0; i < getInvSize(); i++) {
            ret.add(getInvStack(i));
        }
        return ret;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        clear();
        for (int i = 0; i < list.size(); i++) {
            setInvStack(i, list.get(i));
        }
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public boolean isInvEmpty() {
        return getInventories().stream().allMatch(LootableContainerBlockEntity::isInvEmpty);
    }

    @Override
    public ItemStack getInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).getInvStack(targetSlot.getRight());
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).takeInvStack(targetSlot.getRight(), amount);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeInvStack(targetSlot.getRight());
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        getInventories().get(targetSlot.getLeft()).setInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public int getInvMaxStackAmount() {
        return UnifiedStorage.MAX_STACK_SIZE;
    }

    @Override
    public void markDirty() {
        getInventories().forEach(LootableContainerBlockEntity::markDirty);
    }

    @Override
    public void onInvOpen(PlayerEntity player) {
        //getInventories().get(0).onInvOpen(player);
    }

    @Override
    public void onInvClose(PlayerEntity player) {
        //getInventories().get(0).onInvClose(player);
    }

    @Override
    public boolean isValidInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).isValidInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public void clear() {
        getInventories().forEach(LootableContainerBlockEntity::clear);
    }


    @Override
    public int[] getInvAvailableSlots(Direction side) {
        return IntStream.range(0, getInvSize()).toArray();
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        ItemStack targetStack = getInvStack(slot);
        return targetStack.isEmpty() ||
                (targetStack.getItem() == stack.getItem() &&
                        targetStack.getCount() + stack.getCount() <= this.getInvMaxStackAmount());
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return !getInvStack(slot).isEmpty();
    }

}
