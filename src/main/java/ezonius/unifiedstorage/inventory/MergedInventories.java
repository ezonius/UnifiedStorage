package ezonius.unifiedstorage.inventory;

import ezonius.unifiedstorage.block.entity.STBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class MergedInventories implements Inventory {

    private final ArrayList<STBlockEntity> inventory;
    private int invSize;

    public MergedInventories(ArrayList<STBlockEntity> collect) {
        this.inventory = collect;
        this.invSize = collect.stream().map(STBlockEntity::getInvSize).reduce(Integer::sum).orElse(108);
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
        int index = slot != 0 ? Math.abs(slot / STBlockEntity.INV_SIZE) : 0;
        return inventory.get(index).getInvStack(slot - (index * STBlockEntity.INV_SIZE));
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        int index = slot != 0 ? Math.abs(slot / STBlockEntity.INV_SIZE) : 0;
        return inventory.get(index).takeInvStack(slot - (index * STBlockEntity.INV_SIZE), amount);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        int index = slot != 0 ? Math.abs(slot / STBlockEntity.INV_SIZE) : 0;
        return inventory.get(index).removeInvStack(slot - (index * STBlockEntity.INV_SIZE));
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        int index = slot != 0 ? Math.abs(slot / STBlockEntity.INV_SIZE) : 0;
        inventory.get(index).setInvStack(slot - (index * STBlockEntity.INV_SIZE), stack);
    }

    @Override
    public int getInvMaxStackAmount() {
        return STBlockEntity.MAX_STACK_AMOUNT;
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
        inventory.forEach(entry -> entry.onInvOpen(player));
    }

    @Override
    public void onInvClose(PlayerEntity player) {
        inventory.forEach(entry -> entry.onInvClose(player));
    }

    @Override
    public boolean isValidInvStack(int slot, ItemStack stack) {
        int index = slot != 0 ? Math.abs(slot / STBlockEntity.INV_SIZE) : 0;
        return inventory.get(index).isValidInvStack(slot - (index * STBlockEntity.INV_SIZE), stack);
    }

    @Override
    public void clear() {
        inventory.forEach(LootableContainerBlockEntity::clear);
    }


}
