package ezonius.unifiedstorage.mixins;

import ezonius.unifiedstorage.misc.SlotAccessor;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public final class SlotMixin implements SlotAccessor
{
	@Mutable @Shadow @Final public int xPosition;
	@Mutable @Shadow @Final public int yPosition;

	@Override
	public final void setX(int x) { xPosition = x; }

	@Override
	public final void setY(int y) { yPosition = y; }
}
