package ezonius.unifiedstorage.block;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class StorageInterfaceBlock extends BlockWithEntity {
    protected StorageInterfaceBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return null;
    }
}
