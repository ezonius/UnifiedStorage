package ezonius.unifiedstorage.client.gui.screen.ingame;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.container.ScrollableContainer;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ScrollableScreen extends CottonInventoryScreen<ScrollableContainer> {
    public ScrollableScreen(ScrollableContainer container, PlayerEntity player) {
        super(container, player);

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        repositionInventorySorterWidget();
    }

    private void repositionInventorySorterWidget() {
        for (int i = 0; i < this.buttons.size(); i++) {
            AbstractButtonWidget button = this.buttons.get(i);
            if (button.getClass().getName().equals("SortButtonWidget")) {
                button.x = this.x + this.backgroundWidth - 12;
                if (i == 0) {
                    button.y = this.y + backgroundHeight - 83 - (Math.min((this.handler.getInventory().size() / 9), UnifiedStorage.MAX_ROWS) + 1) * 18;
                }
                if (i == 1) {
                    button.y = this.y + backgroundHeight - 88;
                }
            }
        }
    }
}
