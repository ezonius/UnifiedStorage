package ezonius.unifiedstorage.mixins;

import ezonius.unifiedstorage.misc.SlotAccessor;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 *
 * @author NinjaPhenix
 */
@Mixin(Slot.class)
public final class SlotMixin implements SlotAccessor
{
	@Mutable @Shadow @Final public int xPosition;
	@Mutable @Shadow @Final public int yPosition;
	@Mutable @Shadow @Final private int invSlot;

	@Override
	public final void setX(int x) { xPosition = x; }

	@Override
	public final void setY(int y) { yPosition = y; }

	@Override
	public final int getInvSlot() {
		return invSlot;
	}
}
