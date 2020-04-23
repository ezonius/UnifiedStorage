package ezonius.unifiedstorage.block.entity;

import ezonius.unifiedstorage.inventory.MergedInventoriesInterface;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void onInvOpen(PlayerEntity player) {
        //getInventories().get(0).onInvOpen(player);
    }

    @Override
    public void onInvClose(PlayerEntity player) {
        //getInventories().get(0).onInvClose(player);
    }

    // **************************************
    // LootableContainerBlockEntity overrides
    // **************************************

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        DefaultedList<ItemStack> ret = DefaultedList.of();
        for (int i = 0; i < getInvSize(); i++) {
            ret.add(getInvStack(i));
        }
        return ret;
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        clear();
        for (int i = 0; i < list.size(); i++) {
            setInvStack(i, list.get(i));
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
    public void setInvStack(int slot, ItemStack stack) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        getInventories().get(targetSlot.getLeft()).setInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).removeInvStack(targetSlot.getRight());
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).takeInvStack(targetSlot.getRight(), amount);
    }

    @Override
    public ItemStack getInvStack(int slot) {
        Pair<Integer, Integer> targetSlot = getInvSlotMap().get(slot);
        return getInventories().get(targetSlot.getLeft()).getInvStack(targetSlot.getRight());
    }

    @Override
    public boolean isInvEmpty() {
        return getInventories().stream().allMatch(LootableContainerBlockEntity::isInvEmpty);
    }
}
