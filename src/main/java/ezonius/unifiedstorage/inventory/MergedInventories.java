package ezonius.unifiedstorage.inventory;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.STBlock;
import ezonius.unifiedstorage.block.entity.STBlockEntity;
import ezonius.unifiedstorage.modules.STModule;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MergedInventories implements Inventory {

    private final ArrayList<STBlockEntity> inventory;
    private final int invSize;
    private final HashMap<Integer, Pair<Integer, Integer>> invSlotMap;

    public MergedInventories(ArrayList<STBlockEntity> collect, int invSize) {
        this.inventory = collect;
        this.invSize = collect.stream().map(STBlockEntity::getInvSize).reduce(Integer::sum).orElse(invSize);
        this.invSlotMap = new HashMap<>();
        var slot = 0;
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
        var targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).getInvStack(targetSlot.getRight());
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        var targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).takeInvStack(targetSlot.getRight(), amount);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        var targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).removeInvStack(targetSlot.getRight());
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        var targetSlot = invSlotMap.get(slot);
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
        var targetSlot = invSlotMap.get(slot);
        return inventory.get(targetSlot.getLeft()).isValidInvStack(targetSlot.getRight(), stack);
    }

    @Override
    public void clear() {
        inventory.forEach(LootableContainerBlockEntity::clear);
    }


}
