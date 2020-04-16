package ezonius.unifiedstorage.widgets;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.inventory.Inventory;

public class WScrollInv_ItemSlot extends WItemSlot {
    private Inventory inventory;
    private int startIndex;

    public WScrollInv_ItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big,
                               boolean ltr) {
        super(inventory, startIndex, slotsWide, slotsHigh, big, ltr);
    }

    @Override
    public void onMouseScroll(int x, int y, double amount) {
        this.parent.onMouseScroll(x, y, amount);
    }
}
