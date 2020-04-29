package ezonius.unifiedstorage.widgets;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

public class WScrollInv_TextField extends WTextField {
    
    private boolean canResize;
    
    public WScrollInv_TextField(Text suggestion) {
        this.suggestion = suggestion.asString();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onCharTyped(char ch) {
        super.onCharTyped(ch);
    }

    @Override
    public void setText(String s) {
        super.setText(s);
        if (this.parent instanceof WScrollInv) {
            WScrollInv parent = ((WScrollInv) this.parent);
            parent.mapSlotsToSorted();
            if (parent.scrollbar != null) {
                parent.scrollbar.setValue(0);
            } else {
                parent.repositionScrollInvSlots(0);
            }
        }
    }
    
    @Override
    public boolean canResize() {
        return this.canResize;
    }
    
    public void setCanResize(boolean canResize) {
        this.canResize = canResize;
    }
    
    @Override
    public void setSize(int x, int y) {
        this.width = x;
        this.height = y;
    }
}
