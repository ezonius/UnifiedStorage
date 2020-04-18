package ezonius.unifiedstorage.widgets;

import ezonius.unifiedstorage.inventory.MergedInventories;
import ezonius.unifiedstorage.misc.SlotAccessor;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class WScrollInv extends WGridPanel {
    private final int maxRows;
    private final CottonCraftingController hostController;
    private final int slotsWide;
    private final int rows;
    private final int scrollSize;
    final WScrollInv_ScrollBar scrollbar;
    final WScrollInv_TextField searchField;
    private final Inventory blockInventory;
    private ArrayList<Integer> unsortedToSortedSlotMap = new ArrayList<>();
    private HashMap<Integer, Integer> sortedSlotMap = new HashMap<>();

    public WScrollInv(Inventory blockInventory, int slotsWide, int maxRows, CottonCraftingController hostController) {
        this.blockInventory = blockInventory;
        this.slotsWide = slotsWide;
        this.maxRows = maxRows;
        this.hostController = hostController;
        this.rows = computeRows(this.blockInventory, this.slotsWide);
        this.scrollSize = computeMaxScrollValue(this.blockInventory, this.slotsWide);
        for (int i = 0; i < blockInventory.getInvSize(); i++) {
            unsortedToSortedSlotMap.add(i);
        }

        // Container Inventory
        var visibleSlots = new WScrollInv_ItemSlot(this.blockInventory, 0, this.slotsWide, this.maxRows, false, false);
        var invisibleSlots = new WScrollInv_ItemSlot(this.blockInventory, this.slotsWide * maxRows, this.slotsWide,
                (this.rows) - this.maxRows, false, false);
        invisibleSlots.setShouldExpandToFit(false);
        this.add(visibleSlots, 0, 0);
        this.add(invisibleSlots, 0, 2000);

        // Scroll Bar
        this.scrollbar = new WScrollInv_ScrollBar(Axis.VERTICAL);
        this.scrollbar.setSize(8, 18 * this.maxRows);
        this.scrollbar.setWindow(1);
        this.scrollbar.setMaxValue(scrollSize);
        this.scrollbar.setLocation(18, 18);
        this.add(this.scrollbar, this.slotsWide, 0);

        // Search Field
        this.searchField = new WScrollInv_TextField(new TranslatableText("itemGroup.search"));
        this.searchField.setBackgroundColor(Color.gray.getRGB());
        this.searchField.setDisabledColor(Color.red.getRGB());
        this.searchField.setEnabledColor(Color.white.getRGB());
        this.searchField.setSuggestionColor(Color.darkGray.getRGB());
        this.searchField.setDrawFocusBorder(false);
        this.searchField.setDrawTextWithShadow(false);
        this.searchField.setBackgroundPainter(BackgroundPainter.SLOT);
        this.searchField.setSize(5 * 18 - 2, 10);
        this.searchField.setResize(false);

        this.add(this.searchField, 0, maxRows);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onMouseScroll(int x, int y, double amount) {
        if (isWithinBounds(x, y))
            scrollbar.setValue((int) (scrollbar.getValue() - amount));
        super.onMouseScroll(x, y, amount);
    }

    /**
     * Reposition the inventory slots so that the correct rows are shown and the hidden slots
     * are moved outside of view.
     * @param scrollValue the new absolute scroll value
     */
    protected void repositionScrollInvSlots(int scrollValue) {
        for (int slotsIndex = 0; slotsIndex < hostController.slots.size(); slotsIndex++) {
            ValidatedSlot entry = (ValidatedSlot) hostController.slots.get(slotsIndex);
            if (!(entry.inventory instanceof MergedInventories))
                continue;
            int targetIndex = this.searchField.getText().isEmpty() ? entry.getInventoryIndex() :
                    sortedSlotMap.get(entry.getInventoryIndex());
            repositionSlot(scrollValue, slotsIndex, targetIndex);
        }
    }

    /**
     * Reposition inventory slot to a new slot position
     * @param scrollValue the absolute scroll value
     * @param slotsIndex the index of the slots to be moved
     * @param targetSlot the index of the target slot
     */
    protected void repositionSlot(int scrollValue, int slotsIndex, int targetSlot) {
        var x = (targetSlot % this.slotsWide);
        var y = Math.abs(targetSlot / this.slotsWide);
        final SlotAccessor accessor = (SlotAccessor) hostController.slots.get(slotsIndex);
        accessor.setX(x * 18);
        accessor.setY(y >= scrollValue && y < scrollValue + this.maxRows ? (y - scrollValue) * 18 + getY() : 3000);
    }

    /**
     * Generates a new map for slot positions based on search field
     */
    protected void mapSlotsToSorted() {
        if (searchField.getText().isEmpty()) {
            unsortedToSortedSlotMap.sort(Integer::compareTo);
        } else {
            unsortedToSortedSlotMap.sort(this::compareStackToSearch);
        }
        sortedSlotMap.clear();
        for (int i = 0; i < unsortedToSortedSlotMap.size(); i++) {
            sortedSlotMap.putIfAbsent(unsortedToSortedSlotMap.get(i), i);
        }
    }

    /**
     * Compare function for search field. Will first sort of search field matches
     * and then sort by name
     * @author Partly by NinjaPhenix
     */
    protected int compareStackToSearch(Integer a, Integer b) {
        if (a == null || b == null) { return 0; }
        final ItemStack stack_a = blockInventory.getInvStack(a);
        final ItemStack stack_b = blockInventory.getInvStack(b);
        if (stack_a.isEmpty() && !stack_b.isEmpty()) { return 1; }
        if (!stack_a.isEmpty() && stack_b.isEmpty()) { return -1; }
        if (stack_a.isEmpty()) { return 0; }
        final boolean stack_a_matches = stack_a.getName().getString().toLowerCase().contains(searchField.getText().toLowerCase());
        final boolean stack_b_matches = stack_b.getName().getString().toLowerCase().contains(searchField.getText().toLowerCase());
        if ((stack_a_matches && stack_b_matches) || (!stack_a_matches && !stack_b_matches)){
            return stack_a.getName().getString().compareTo(stack_b.getName().getString());
        }
        if (stack_b_matches){
            return 1;
        }
        return -1;
    }

    /**
     * Computes the amount of rows for the inventory
     * @param inventory the inventory
     * @param slotsWide how many slots wide a row is
     * @return amount of rows
     */
    private int computeRows(Inventory inventory, int slotsWide) {
        return inventory.getInvSize() / slotsWide;
    }

    /**
     * Computes the max scroll value for a given inventory and row length
     * @param inventory the given inventory
     * @param slotsWide row length
     * @return max scroll value
     */
    private int computeMaxScrollValue(Inventory inventory, int slotsWide) {
        return Math.max(this.computeRows(inventory, slotsWide) - maxRows + 1, 0);
    }

    @Override
    public void onKeyPressed(int ch, int key, int modifiers) {
        super.onKeyPressed(ch, key, modifiers);

    }


}
