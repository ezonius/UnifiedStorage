package ezonius.unifiedstorage.UnifiedStorageController;

import ezonius.unifiedstorage.UnifiedStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class USCBlockEntity extends BlockEntity implements USCImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public USCBlockEntity() {
        super(UnifiedStorage.USC_BLOCK_ENTITY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState state) {
        return true;
    }













    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        Inventories.fromTag(tag, getItems());
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, getItems());
        return super.toTag(tag);
    }

}
