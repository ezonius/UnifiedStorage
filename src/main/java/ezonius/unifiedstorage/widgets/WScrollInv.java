package ezonius.unifiedstorage.widgets;

import ezonius.unifiedstorage.misc.SlotAccessor;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class WScrollInv extends WPlainPanel {
    private final int maxRows;
    private final CottonCraftingController hostController;
    private final int slotsWide;
    private final int rows;
    private final int scrollSize;
    WScrollInv_ScrollBar scrollbar;
    final WScrollInv_TextField searchField;
    private final Inventory blockInventory;
    private final ArrayList<Integer> unsortedToSortedSlotMap = new ArrayList<>();
    private final HashMap<Integer, Integer> sortedSlotMap = new HashMap<>();

    public WScrollInv(Inventory blockInventory, int slotsWide, int maxRows, CottonCraftingController hostController) {
        this.blockInventory = blockInventory;
        this.slotsWide = slotsWide;
        this.maxRows = maxRows;
        this.hostController = hostController;
        this.rows = computeRows(this.blockInventory, this.slotsWide);
        this.scrollSize = computeMaxScrollValue(this.blockInventory, this.slotsWide);
        for (int i = 0; i < blockInventory.size(); i++) {
            unsortedToSortedSlotMap.add(i);
        }

        // Container Inventory
        WScrollInv_ItemSlot visibleSlots = new WScrollInv_ItemSlot(this.blockInventory, 0, this.slotsWide, Math.min(rows, maxRows), false, false);
        visibleSlots.setSize(this.slotsWide * 18, this.maxRows * 18);
        this.add(visibleSlots, 0, 0);
        if (rows > maxRows) {
            WScrollInv_ItemSlot invisibleSlots = new WScrollInv_ItemSlot(this.blockInventory, this.slotsWide * maxRows, this.slotsWide,
                    (this.rows) - this.maxRows, false, false);
            invisibleSlots.setShouldExpandToFit(false);
            this.add(invisibleSlots, 0, 3000);
        }


        // Scroll Bar
        this.scrollbar = new WScrollInv_ScrollBar(Axis.VERTICAL);
        this.scrollbar.setSize(15, 18 * Math.min(rows, maxRows));
        this.scrollbar.setWindow(1);
        this.scrollbar.setMaxValue(scrollSize);
        this.scrollbar.setLocation(this.slotsWide * 18, 0);
        this.add(this.scrollbar, this.slotsWide * 18, 0);

        // Search Field
        this.searchField = new WScrollInv_TextField(new TranslatableText("itemGroup.search"));
        this.searchField.setBackgroundColor(Color.gray.getRGB());
        this.searchField.setDisabledColor(Color.red.getRGB());
        this.searchField.setEnabledColor(Color.white.getRGB());
        this.searchField.setSuggestionColor(Color.darkGray.getRGB());
        this.searchField.setDrawFocusBorder(false);
        this.searchField.setDrawTextWithShadow(false);
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
            //noinspection MethodCallSideOnly
            this.searchField.setBackgroundPainter(BackgroundPainter.SLOT);
        this.searchField.setSize(5 * 18 - 2, 9);
        this.searchField.setCanResize(false);
        this.add(this.searchField, 4 * 18, Math.min(rows, maxRows) * 18 + 1, 5 * 18 - 2, 9);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void tick() {
        super.tick();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onMouseScroll(int x, int y, double amount) {
        if (scrollbar != null && isWithinBounds(x, y))
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
            if (!(entry.inventory instanceof LootableContainerBlockEntity))
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
        int x = (targetSlot % this.slotsWide);
        int y = Math.abs(targetSlot / this.slotsWide);
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
        final ItemStack stack_a = blockInventory.getStack(a);
        final ItemStack stack_b = blockInventory.getStack(b);
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
        return inventory.size() / slotsWide;
    }

    /**
     * Computes the max scroll value for a given inventory and row length
     * @param inventory the given inventory
     * @param slotsWide row length
     * @return max scroll value
     */
    private int computeMaxScrollValue(Inventory inventory, int slotsWide) {
        return Math.max(this.computeRows(inventory, slotsWide) - maxRows + 1, 1);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onKeyPressed(int ch, int key, int modifiers) {
        super.onKeyPressed(ch, key, modifiers);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick(int x, int y, int button) {
        requestFocus();
        super.onClick(x, y, button);
    }

    @Override
    public boolean canFocus() {
        return true;
    }


}
