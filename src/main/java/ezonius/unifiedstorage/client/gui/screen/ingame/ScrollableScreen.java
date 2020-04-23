package ezonius.unifiedstorage.client.gui.screen.ingame;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.container.ScrollableContainer;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ScrollableScreen extends CottonInventoryScreen<ScrollableContainer> {
    public ScrollableScreen(ScrollableContainer container, PlayerEntity player) {
        super(container, player);

    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (super.keyPressed(ch, keyCode, modifiers))
            return true;

        this.handleHotbarKeyPressed(ch, keyCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (Objects.requireNonNull(this.minecraft).options.keyPickItem.matchesKey(ch, keyCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.minecraft.options.keyDrop.matchesKey(ch, keyCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        repositionInventorySorterWidget();
    }

    private void repositionInventorySorterWidget() {
        for (int i = 0; i < this.buttons.size(); i++) {
            AbstractButtonWidget button = this.buttons.get(i);
            if (button.getClass().getName().equals("SortButtonWidget")) {
                button.x = this.x + this.containerWidth - 12;
                if (i == 0) {
                    button.y = this.y + containerHeight - 83 - (Math.min((this.container.getInventory().getInvSize() / 9), UnifiedStorage.MAX_ROWS) + 1) * 18;
                }
                if (i == 1) {
                    button.y = this.y + containerHeight - 88;
                }
            }
        }
    }
}
