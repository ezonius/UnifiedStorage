package ezonius.unifiedstorage.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class StorageInterfaceBlockEntity extends BlockEntityMergedInventory {

    public StorageInterfaceBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.storageinterface");
    }
}
