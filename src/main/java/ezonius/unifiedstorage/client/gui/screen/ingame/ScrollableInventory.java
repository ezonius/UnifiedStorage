package ezonius.unifiedstorage.client.gui.screen.ingame;

import ezonius.unifiedstorage.block.entity.STBlockEntity;
import ezonius.unifiedstorage.misc.SlotAccessor;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.inventory.Inventory;

public class ScrollableInventory extends WGridPanel {
    private final int maxRows;
    private final STBlockController stBlockController;
    private final int slotsWide;
    private final int rows;
    private final int scrollSize;
    private final CustomScrollBar scrollbar;
    private final Inventory blockInventory;
    private WItemSlot wItemSlot;
    private int scrollValue = 0;
    public ScrollableInventory(Inventory blockInventory, int slotsWide, int maxRows, STBlockController stBlockController) {
        this.blockInventory = blockInventory;
        this.slotsWide = slotsWide;
        this.maxRows = maxRows;
        this.stBlockController = stBlockController;
        this.rows = getRows(this.blockInventory, this.slotsWide);
        this.scrollSize = getScrollSize(this.blockInventory, this.slotsWide);

        // Title
        WDynamicLabel dynamicLabel = new WDynamicLabel(() -> "Storage Terminal");
        this.add(dynamicLabel, 0, -1);

        // Container Inventory
        var wItemSlot = WItemSlot.of(this.blockInventory, 0, this.slotsWide, this.maxRows);
        this.add(wItemSlot, 0, 0);

        // Scroll Bar
        this.scrollbar = new CustomScrollBar(Axis.VERTICAL);
        this.scrollbar.setSize(8, 18 * this.maxRows);
        this.scrollbar.setWindow(1);
        this.scrollbar.setMaxValue(scrollSize);
        this.scrollbar.setLocation(18, 18);
        this.add(this.scrollbar, this.slotsWide, 0);
    }

    @Override
    public void paintBackground(int x, int y, int mouseX, int mouseY) {
        super.paintBackground(x, y, mouseX, mouseY);
    }

    @Override
    public void tick() {
        var scrollValue = scrollbar.getValue();
        if (this.scrollValue != scrollValue) {
            for (int i = 0; i < stBlockController.slots.size(); i++) {
                ValidatedSlot entry = (ValidatedSlot) stBlockController.slots.get(i);
                int inventoryIndex = entry.getInventoryIndex();
                if (!(entry.inventory instanceof STBlockEntity)) continue;
                var x = (inventoryIndex % this.slotsWide);
                var y = Math.abs(inventoryIndex / this.slotsWide);
                final SlotAccessor accessor = (SlotAccessor) stBlockController.slots.get(i);
                if (y >= scrollValue && y < scrollValue + this.maxRows) {
                    accessor.setX(x * 18);
                    accessor.setY((y - scrollValue) * 18);
                } else {
                    accessor.setX(x * 18);
                    accessor.setY(y * 18 + 2000);
                }
            }
            this.scrollValue = scrollValue;
        }
        super.tick();
    }

    private int getRows(Inventory inventory, int slotsWide) {
        return inventory.getInvSize() / slotsWide;
    }

    private int getScrollSize(Inventory inventory, int slotsWide) {
        int slotsHigh = (inventory.getInvSize() / slotsWide);
        return Math.max(slotsHigh - maxRows + 1, 0);
    }
}
