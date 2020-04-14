package ezonius.unifiedstorage.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;

import java.util.List;
import java.util.function.BiConsumer;

public class STBlockController extends CottonCraftingController {
    private final int maxRows = 6;
    private final int slotsWide = 9;
    private final int rows;
    private final int scrollSize;
    public STBlockController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        this.rows = getRows(blockInventory, slotsWide);
        this.scrollSize = getScrollSize(blockInventory, slotsWide);
        ScrollableInventory scrollableInventory = new ScrollableInventory(blockInventory, slotsWide, maxRows, this);

        root.add(scrollableInventory, 0, 0);
        root.add(this.createPlayerInventoryPanel(), 0,this.maxRows + 1);
        root.validate(this);

        // Add slots for the rest of blockInventory and move them outside of view.
        // This is because the scrolling functions works by moving the slots around.
        // If they are added through normal LibGUI elements, it will mess up the texture generation.
        for (int i = this.slotsWide * this.maxRows; i < blockInventory.getInvSize(); i++) {
            var x = (i % this.slotsWide);
            var y = Math.abs(i / this.slotsWide);
            this.addSlotPeer(new ValidatedSlot(blockInventory, i, x * 18, y * 18 + 2000));
        }
    }
    private int getRows(Inventory inventory, int slotsWide) {
        int slotsHigh = inventory.getInvSize() / slotsWide;
        return Math.min(slotsHigh, maxRows);
    }

    private int getScrollSize(Inventory inventory, int slotsWide) {
        int slotsHigh = (inventory.getInvSize() / slotsWide) + 1;
        return slotsHigh - maxRows > 0 ? slotsHigh : 0;
    }
}
