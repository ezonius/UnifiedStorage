package ezonius.unifiedstorage.widgets;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.inventory.Inventory;

public class WScrollInv_ItemSlot extends WItemSlot {
    private boolean valid = false;

    public WScrollInv_ItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big,
                               boolean ltr) {
        super(inventory, startIndex, slotsWide, slotsHigh, big, ltr);
    }

    public static WScrollInv_ItemSlot of(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
        return new WScrollInv_ItemSlot(inventory, startIndex, slotsWide, slotsHigh, false, false);
    }

    public static WScrollInv_ItemSlot ofPlayerStorage(Inventory inventory) {
        return new WScrollInv_ItemSlot(inventory, 9, 9, 3, false, false);
    }

    @Override
    public void onMouseScroll(int x, int y, double amount) {
        this.parent.onMouseScroll(x, y, amount);
    }

    @Override
    public void onClick(int x, int y, int button) {
        requestFocus();
        super.onClick(x, y, button);
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    @Override
    public void createPeers(GuiDescription c) {
        if (!isValid()) {
            super.createPeers(c);
            setValid(true);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
