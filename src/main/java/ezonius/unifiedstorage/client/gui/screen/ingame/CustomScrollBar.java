package ezonius.unifiedstorage.client.gui.screen.ingame;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

public class CustomScrollBar extends WScrollBar {
    protected Axis axis;
    protected int value;
    protected int maxValue;
    protected int window;
    protected int anchor;
    protected int anchorValue;
    protected boolean sliding;

    public CustomScrollBar() {
        this.axis = Axis.HORIZONTAL;
        this.maxValue = 100;
        this.window = 16;
        this.anchor = -1;
        this.anchorValue = -1;
        this.sliding = false;
    }

    public CustomScrollBar(Axis axis) {
        this.axis = Axis.HORIZONTAL;
        this.maxValue = 100;
        this.window = 16;
        this.anchor = -1;
        this.anchorValue = -1;
        this.sliding = false;
        this.axis = axis;
    }

    public void paintBackground(int x, int y, int mouseX, int mouseY) {
        if (LibGuiClient.config.darkMode) {
            ScreenDrawing.drawBeveledPanel(x, y, this.width, this.height, -14606047, -13684945, -10658467);
        } else {
            ScreenDrawing.drawBeveledPanel(x, y, this.width, this.height, -13158601, -7631989, -1);
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
                ScreenDrawing.drawBeveledPanel(x + 1 + this.getHandlePosition(), y + 1, this.getHandleSize(), this.height - 2, top, middle, bottom);
            } else {
                ScreenDrawing.drawBeveledPanel(x + 1, y + 1 + this.getHandlePosition(), this.width - 2, this.getHandleSize(), top, middle, bottom);
            }

        }
    }

    public int getHandleSize() {
        float percentage = this.window >= this.maxValue ? 1.0F : (float)this.window / (float)this.maxValue;
        int bar = this.axis == Axis.HORIZONTAL ? this.width - 2 : this.height - 2;
        int result = (int)(percentage * (float)bar);
        if (result < 6) {
            result = 6;
        }

        return result;
    }

    public int getMovableDistance() {
        int bar = this.axis == Axis.HORIZONTAL ? this.width -2 : this.height -2;
        return bar - this.getHandleSize();
    }

    @Override
    public int pixelsToValues(int pixels) {
        int bar = this.axis == Axis.HORIZONTAL ? (int) Math.rint(this.width * 0.9) : (int) Math.rint(this.height * 0.9);
        float percent = (float)pixels / (float)bar;
        return (int)(percent * (float)(this.maxValue - this.window));
    }

    public int getHandlePosition() {
        float percent = (float)this.value / (float)Math.max(this.maxValue - this.window, 1);
        return (int)(percent * (float)this.getMovableDistance());
    }

    public int getMaxScrollValue() {
        return this.maxValue - this.window;
    }

    protected void adjustSlider(int x, int y) {
        int delta = 0;
        if (this.axis == Axis.HORIZONTAL) {
            delta = x - this.anchor;
        } else {
            delta = y - this.anchor;
        }

        int valueDelta = this.pixelsToValues(delta);
        int valueNew = this.anchorValue + valueDelta;
        if (valueNew > this.getMaxScrollValue()) {
            valueNew = this.getMaxScrollValue();
        }

        if (valueNew < 0) {
            valueNew = 0;
        }

        this.value = valueNew;
    }

    @Override
    public void onMouseScroll(int x, int y, double amount) {
        if (isWithinBounds(x, y))
            setValue((int) (getValue() - amount));
    }

    @Override
    public WWidget onMouseDown(int x, int y, int button) {
        adjustSlider(x, y);
        if (this.axis == Axis.HORIZONTAL) {
            this.anchor = x;
            this.anchorValue = this.value;
        } else {
            this.anchor = y;
            this.anchorValue = this.value;
        }

        this.sliding = true;
        return this;
    }

    public void onMouseDrag(int x, int y, int button) {
        this.adjustSlider(x, y);
    }

    public WWidget onMouseUp(int x, int y, int button) {
        this.anchor = -1;
        this.anchorValue = -1;
        this.sliding = false;
        return this;
    }

    public int getValue() {
        return this.value;
    }

    public WScrollBar setValue(int value) {
        this.value = value;
        this.checkValue();
        return this;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public WScrollBar setMaxValue(int max) {
        this.maxValue = max;
        this.checkValue();
        return this;
    }

    public int getWindow() {
        return this.window;
    }

    public WScrollBar setWindow(int window) {
        this.window = window;
        return this;
    }

    protected void checkValue() {
        if (this.value > this.maxValue - this.window) {
            this.value = this.maxValue - this.window;
        }

        if (this.value < 0) {
            this.value = 0;
        }

    }
}
