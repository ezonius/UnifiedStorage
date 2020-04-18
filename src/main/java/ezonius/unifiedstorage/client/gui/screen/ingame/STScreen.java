package ezonius.unifiedstorage.client.gui.screen.ingame;

import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class STScreen extends CottonInventoryScreen<STBlockController> {
    public STScreen(STBlockController container, PlayerEntity player) {
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
                this.onMouseClick(this.focusedSlot, ((ValidatedSlot)focusedSlot).getInventoryIndex(), hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }

        return true;
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        if (slot != null) {
            invSlot = slot.id;
        }

        Objects.requireNonNull(Objects.requireNonNull(this.minecraft).interactionManager).clickSlot(this.container.syncId, invSlot, button, slotActionType, this.minecraft.player);
    }

    @Override
    protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
        return super.handleHotbarKeyPressed(keyCode, scanCode);
    }


}
