package ezonius.unifiedstorage.widgets;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.text.Text;

public class WScrollInv_TextField extends WTextField {

    public WScrollInv_TextField(Text suggestion) {
        this.suggestion = suggestion.asString();
    }

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
}
