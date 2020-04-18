package ezonius.unifiedstorage.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

import java.util.Set;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 *
 * Originally by Juuz
 */
@FunctionalInterface
public interface ImplementedInventory extends Inventory {
    /**
     * Gets the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    DefaultedList<ItemStack> getItems();
    // Creation
    /**
     * Creates an inventory from the item list.
     */
    static ImplementedInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }
    /**
     * Creates a new inventory with the size.
     */
    static ImplementedInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }
    // Inventory
    /**
     * Returns the inventory size.
     */
    @Override
    default int getInvSize() {
        return getItems().size();
    }
    /**
     * @return true if this inventory has only empty stacks, false otherwise
     */
    @Override
    default boolean isInvEmpty() {
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    /**
     * Gets the item in the slot.
     */
    @Override
    default ItemStack getInvStack(int slot) {
        return getItems().get(slot);
    }
    /**
     * Takes a stack of the size from the slot.
     * <p>(default implementation) If there are less items in the slot than what are requested,
     * takes all items in that slot.
     */
    @Override
    default ItemStack takeInvStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }
    /**
     * Removes the current stack in the {@code slot} and returns it.
     */
    @Override
    default ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }
    /**
     * Replaces the current stack in the {@code slot} with the provided stack.
     * <p>If the stack is too big for this inventory ({@link Inventory#getInvMaxStackAmount()}),
     * it gets resized to this inventory's maximum amount.
     */
    @Override
    default void setInvStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getInvMaxStackAmount()) {
            stack.setCount(getInvMaxStackAmount());
        }
    }
    /**
     * Clears {@linkplain #getItems() the item list}}.
     */
    @Override
    default void clear() {
        getItems().clear();
    }
    @Override
    default void markDirty() {
        // Override if you want behavior.
    }

    @Override
    default boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    default void onInvOpen(PlayerEntity player) {
    }

    @Override
    default void onInvClose(PlayerEntity player) {
    }

    @Override
    default int getInvMaxStackAmount() {
        return 64;
    }

    @Override
    default boolean isValidInvStack(int slot, ItemStack stack) {
        return true;
    }

    @Override
    default int countInInv(Item item) {
        int i = 0;

        for(int j = 0; j < this.getInvSize(); ++j) {
            ItemStack itemStack = this.getInvStack(j);
            if (itemStack.getItem().equals(item)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    @Override
    default boolean containsAnyInInv(Set<Item> items) {
        for(int i = 0; i < this.getInvSize(); ++i) {
            ItemStack itemStack = this.getInvStack(i);
            if (items.contains(itemStack.getItem()) && itemStack.getCount() > 0) {
                return true;
            }
        }

        return false;
    }
}

