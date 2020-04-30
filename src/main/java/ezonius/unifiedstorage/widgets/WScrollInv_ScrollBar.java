package ezonius.unifiedstorage.widgets;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class WScrollInv_ScrollBar extends WScrollBar {

    public WScrollInv_ScrollBar(Axis axis) {
        this.axis = Axis.HORIZONTAL;
        this.maxValue = 100;
        this.window = 16;
        this.anchor = -1;
        this.anchorValue = -1;
        this.sliding = false;
        this.axis = axis;
    }

    @Override
    public void paintBackground(int x, int y, int mouseX, int mouseY) {
        if (LibGuiClient.config.darkMode) {
            ScreenDrawing.drawBeveledPanel(x, y - 1 , this.width, this.height, -14606047, -13684945, -10658467);
        } else {
            ScreenDrawing.drawBeveledPanel(x, y - 1, this.width, this.height, -13158601, -7631989, -1);
        }

        if (this.maxValue > 0) {
            int top;
            int middle;
            int bottom;
            if (this.sliding) {
                if (LibGuiClient.config.darkMode) {
                    top = -9671572;
                    middle = -13684945;
                    bottom = -14606047;
                } else {
                    top = -1;
                    middle = -7631989;
                    bottom = -11184811;
                }
            } else if (this.isWithinBounds(mouseX, mouseY)) {
                if (LibGuiClient.config.darkMode) {
                    top = -10524003;
                    middle = -13484178;
                    bottom = -16048054;
                } else {
                    top = -3157769;
                    middle = -7892537;
                    bottom = -13353355;
                }
            } else if (LibGuiClient.config.darkMode) {
                top = -9671572;
                middle = -12500671;
                bottom = -14606047;
            } else {
                top = -1;
                middle = -3750202;
                bottom = -11184811;
            }

            if (this.axis == Axis.HORIZONTAL) {
                ScreenDrawing.drawBeveledPanel(x + 1 + this.getHandlePosition(), y, this.getHandleSize(), this.height - 2, top, middle, bottom);
            } else {
                ScreenDrawing.drawBeveledPanel(x + 1, y + this.getHandlePosition(), this.width - 2, this.getHandleSize(), top, middle, bottom);
            }

        }
    }

    @Override
    public int pixelsToValues(int pixels) {
        int bar = this.axis == Axis.HORIZONTAL ? (int) Math.rint(this.width * 0.9) : (int) Math.rint(this.height * 0.9);
        float percent = (float)pixels / (float)bar;
        return (int)(percent * (float)(this.maxValue - this.window));
    }

    @Override
    public void onMouseScroll(int x, int y, double amount) {
        if (isWithinBounds(x, y))
            setValue((int) (getValue() - amount));
    }

    @Override
    public void onMouseDrag(int x, int y, int button) {
        super.onMouseDrag(x, y, button);
    }

    @Override
    public WWidget onMouseDown(int x, int y, int button) {
        adjustSlider(x, y);
        return super.onMouseDown(x, y, button);
    }

    @Override
    public WScrollBar setValue(int value) {
        WScrollBar ret = super.setValue(value);
        if (this.parent instanceof WScrollInv) {
            ((WScrollInv) this.parent).repositionScrollInvSlots(this.getValue());
        }
        return ret;
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick(int x, int y, int button) {
        requestFocus();
        super.onClick(x, y, button);
    }
}
