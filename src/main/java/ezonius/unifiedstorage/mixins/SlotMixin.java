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
	@Mutable @Shadow @Final public int x;
	@Mutable @Shadow @Final public int y;
	@Mutable @Shadow @Final private int index;

	@Override
	public final void setX(int x) { this.x = x; }

	@Override
	public final void setY(int y) { this.y = y; }

	@Override
	public final int getIndex() {
		return index;
	}
}
