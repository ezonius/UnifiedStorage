package ezonius.unifiedstorage.misc;

import io.github.cottonmc.cotton.gui.ValidatedSlot;

/**
 * {@link Slot}'s can be casted to {@link SlotAccessor} to allow their xPosition and yPosition to be set.
 *
 * @author NinjaPhenix
 * @since 0.0.1
 */
public interface SlotAccessor
{
    /**
     * Sets the X position for the slot.
     *
     * @param x The new X position
     */
    void setX(int x);

    /**
     * Sets the Y position for the slot.
     *
     * @param y The new Y position
     */
    void setY(int y);

    int getInvSlot();
}
