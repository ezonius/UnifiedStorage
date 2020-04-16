package ezonius.unifiedstorage.client.gui.screen.ingame;

import ezonius.unifiedstorage.block.entity.STBlockEntity;
import ezonius.unifiedstorage.widgets.WScrollInv;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlockContext;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

@Environment(EnvType.CLIENT)
public class STBlockController extends CottonCraftingController {
    private final int maxRows = 6;
    private final int slotsWide = 9;
    private final int rows;
    private final int scrollSize;
    private int titleY = 0;
    private int scrollInvY = titleY + 1;
    private int playerInvY = titleY + maxRows + 2;
    private WScrollInv wScrollInv;
    public STBlockController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        this.rows = this.getRows(blockInventory, slotsWide);
        this.scrollSize = this.getScrollSize(blockInventory, slotsWide);

        // Title
        WDynamicLabel dynamicLabel = new WDynamicLabel(() -> "Storage Terminal");
        root.add(dynamicLabel, 0, titleY);

        // Scrollable Inventory
        wScrollInv = new WScrollInv(blockInventory, slotsWide, maxRows, this);
        root.add(this.wScrollInv, 0, scrollInvY);

        // Player Inventory and Hotbar
        root.add(this.createPlayerInventoryPanel(), 0, playerInvY);

        root.validate(this);
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
