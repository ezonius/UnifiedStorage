package ezonius.unifiedstorage.widgets;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;

import java.util.Objects;

public class WScrollInv_ItemSlot extends WItemSlot {
    private boolean shouldExpandToFit = true;

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

    @Environment(EnvType.CLIENT)
    @Override
    public void onMouseScroll(int x, int y, double amount) {
        Objects.requireNonNull(this.parent).onMouseScroll(x, y, amount);
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
    
    @Override
    public boolean shouldExpandToFit() {
        return this.shouldExpandToFit;
    }
    
    public void setShouldExpandToFit(boolean shouldExpandToFit) {
        this.shouldExpandToFit = shouldExpandToFit;
    }
}
