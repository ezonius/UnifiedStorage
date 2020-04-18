package ezonius.unifiedstorage.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import ezonius.unifiedstorage.block.entity.STBlockEntity;
import ezonius.unifiedstorage.inventory.MergedInventories;
import ezonius.unifiedstorage.widgets.WScrollInv;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class STBlockController extends CottonCraftingController {
    private final int maxRows = 6;
    private final int slotsWide = 9;
    private final int titleY = 0;
    private final int scrollInvY = titleY + 1;
    private final int playerInvY = titleY + maxRows + 2;
    private final WScrollInv wScrollInv;

    public STBlockController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.SMELTING, syncId, playerInventory, new MergedInventories(((STBlockEntity) getBlockInventory(context))
                .getRecursiveAdjacentEntities(((STBlockEntity) getBlockInventory(context))
                        .asSingletonHashSet())
                .collect(Collectors.toCollection(ArrayList::new))), getBlockPropertyDelegate(context));
        blockInventory.onInvOpen(playerInventory.player);

        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        // Title
        WDynamicLabel dynamicLabel = new WDynamicLabel(() -> "Storage Terminal");
        root.add(dynamicLabel, 0, titleY);

        // Scrollable Inventory
        wScrollInv = new WScrollInv(blockInventory, slotsWide, maxRows, this);
        root.add(this.wScrollInv, 0, scrollInvY);

        // Player Inventory and Hotbar
        root.add(this.createPlayerInventoryPanel(), 0, playerInvY);

        if (!this.world.isClient())
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

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        blockInventory.onInvClose(player);
    }

    @Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
        return super.onSlotClick(slotNumber, button, action, player);
    }
}
