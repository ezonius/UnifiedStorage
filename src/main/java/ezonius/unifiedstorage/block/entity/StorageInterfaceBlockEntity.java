package ezonius.unifiedstorage.block.entity;

import ezonius.unifiedstorage.modules.StorageInterfaceModule;
import net.minecraft.block.Block;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.HashSet;

public class StorageInterfaceBlockEntity extends BlockEntityMergedInventory {

    public StorageInterfaceBlockEntity(Block block) {
        super(StorageInterfaceModule.SI_BLOCK_ENTITY_TYPES.get(block));
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.storageinterface");
    }

    @Override
    public HashSet<LootableContainerBlockEntity> asSingletonHashSet() {
        return new HashSet<>(Arrays.asList(this));
    }
}
