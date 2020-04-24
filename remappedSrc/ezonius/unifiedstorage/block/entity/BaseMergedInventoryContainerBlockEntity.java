package ezonius.unifiedstorage.block.entity;

import ezonius.unifiedstorage.inventory.IMergedInventory;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseMergedInventoryContainerBlockEntity extends LootableContainerBlockEntity implements IMergedInventory {
    private ArrayList<LootableContainerBlockEntity> inventories;
    private int invSize;
    private HashMap<Integer, Pair<Integer, Integer>> invSlotMap;

    public BaseMergedInventoryContainerBlockEntity(BlockEntityType<?> type) {
        super(type);
        UpdateInventories(this.getWorld(), this.getPos());
    }

    @Override
    public void UpdateInventories(World world, BlockPos pos) {
        setInventories(calcInventories(world, pos));
        setInvSize(calcInvSize());
        setInvSlotMap(calcInvSlotMap());
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
    public int size() {
        return invSize;
    }

    @Override
    public void setInvSize(int invSize) {
        this.invSize = invSize;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        //getInventories().get(0).onInvOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        //getInventories().get(0).onInvClose(player);
    }

    // **************************************
    // LootableContainerBlockEntity overrides
    // **************************************

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        DefaultedList<ItemStack> ret = DefaultedList.of();
        for (int i = 0; i < size(); i++) {
            ret.add(getStack(i));
        }
        return ret;
    }

    @Override
    protected ScreenHandler createContainer(int i, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        clear();
        for (int i = 0; i < list.size(); i++) {
            setStack(i, list.get(i));
        }
    }

    // **************************************
    // Conflicts between LootableContainerBlockEntity and MergedInventoryInterface
    // **************************************

    @Override
    public void clear() {
        getInventories().forEach(LootableContainerBlockEntity::clear);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        getInventories().get(targetSlot.getLeft()).setStack(targetSlot.getRight(), stack);
    }

    @Override
    public ItemStack removeStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeStack(targetSlot.getRight());
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeStack(targetSlot.getRight(), amount);
    }

    @Override
    public ItemStack getStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).getStack(targetSlot.getRight());
    }

    @Override
    public boolean isEmpty() {
        return getInventories().stream().allMatch(LootableContainerBlockEntity::isEmpty);
    }
}
