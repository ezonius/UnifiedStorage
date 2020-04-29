package ezonius.unifiedstorage.container;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.entity.StorageTerminalBlockEntity;
import ezonius.unifiedstorage.widgets.WScrollInv;
import ezonius.unifiedstorage.widgets.WScrollInv_ItemSlot;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.TranslatableText;

public class ScrollableContainer extends CottonCraftingController {
    private final int slotHeight = 18;
    private final int maxRows = UnifiedStorage.MAX_ROWS;
    private final int slotsWide = UnifiedStorage.ROW_LENGTH;
    private WScrollInv wScrollInv = null;
    public final ScreenHandlerContext context;
    private final int invSize;

    public ScrollableContainer(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, int invSize, boolean mergedInventory) {
        super(RecipeType.SMELTING, syncId, playerInventory,
                getBlockInventory(context),
                getBlockPropertyDelegate(context));
        if (this.blockInventory instanceof StorageTerminalBlockEntity)
            ((StorageTerminalBlockEntity) this.blockInventory).UpdateInventories(((StorageTerminalBlockEntity) this.blockInventory).getWorld(), ((StorageTerminalBlockEntity) this.blockInventory).getPos());
        this.context = context;
        this.invSize = Math.max(this.blockInventory.size(), 0);
        int titleY = 0;
        int scrollInvY = titleY + 11;
        int rows = this.invSize / slotsWide;
        int playerInvY = titleY + (Math.min(rows, maxRows) == 0 ? 3 : Math.min(rows, maxRows)) * 18 + (24);
        blockInventory.onOpen(playerInventory.player);

        // Root
        WPlainPanel root = new WPlainPanel() {
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
        };
        setRootPanel(root);

        // Title
        WLabel dynamicLabel = new WLabel(new TranslatableText("container.unifiedstorage.storage_terminal"));
        root.add(dynamicLabel, 0, titleY);

        // Scrollable Inventory
        if (this.invSize > 0) {
            this.wScrollInv = new WScrollInv(blockInventory, slotsWide, maxRows, this);
            root.add(this.wScrollInv, 0, scrollInvY);
        } else {
            WText noInventoryMsg = new WText(new TranslatableText("container.unifiedstorage.storage_terminal_noInv"));
            noInventoryMsg.setSize(9* 18, 2*18);
            root.add(noInventoryMsg, 0, scrollInvY + 9, 9* 18, 2 * 18);
        }

        // Player Inventory and Hotbar
        WLabel playerInvTitle = new WLabel(new TranslatableText("container.inventory"));
        WScrollInv_ItemSlot playerPanel = WScrollInv_ItemSlot.ofPlayerStorage(playerInventory);
        playerPanel.setSize(3 * slotHeight, 9 * slotHeight);
        WScrollInv_ItemSlot playerHotbar = WScrollInv_ItemSlot.of(playerInventory, 0, 9, 1);
        playerHotbar.setSize(slotHeight, 9 * slotHeight);
        root.add(playerInvTitle, 0, playerInvY - 11);
        root.add(playerPanel, 0, playerInvY);
        root.add(playerHotbar, 0, playerInvY + 58);

        root.validate(this);
    }


    private int getRows(Inventory inventory, int slotsWide) {
        int slotsHigh = inventory.size() / slotsWide;
        return Math.min(slotsHigh, maxRows);
    }

    private int getScrollSize(Inventory inventory, int slotsWide) {
        int slotsHigh = (inventory.size() / slotsWide) + 1;
        return slotsHigh - maxRows > 0 ? slotsHigh : 0;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        blockInventory.onClose(player);
    }

    public Inventory getInventory() {
        return getBlockInventory(context);
    }
}
