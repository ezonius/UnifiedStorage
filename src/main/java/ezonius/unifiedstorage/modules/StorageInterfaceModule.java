package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.StorageInterfaceBlock;
import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
import ezonius.unifiedstorage.block.entity.StorageInterfaceBlockEntity;
import ezonius.unifiedstorage.client.gui.screen.ingame.ScrollableContainer;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.BlockContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class StorageInterfaceModule implements InitModule {
    public static HashMap<Block, BlockEntityType<StorageInterfaceBlockEntity>> SI_BLOCK_ENTITY_TYPES = new HashMap<>();

    @Override
    public void initCommon() {
        RegisterSIBlock(UnifiedStorage.SI_BLOCKID, UnifiedStorage.SI_BLOCK);
    }

    public static void RegisterSIBlock(Identifier blockId, Block block) {
        Registry.register(Registry.BLOCK, blockId, block);
        SI_BLOCK_ENTITY_TYPES.put(block, Registry.register(Registry.BLOCK_ENTITY_TYPE, blockId,
                BlockEntityType.Builder.create((() -> new StorageInterfaceBlockEntity(block)), block).build(null)));

        ContainerProviderRegistry.INSTANCE.registerFactory(blockId,
                (syncId, identifier, player, packetByteBuf) ->
                        new ScrollableContainer(syncId, player.inventory, BlockContext.create(player.world, packetByteBuf.readBlockPos()), 0, false));

        Registry.register(Registry.ITEM, blockId, new BlockItem(block, new Item.Settings().group(ItemGroup.INVENTORY)));
    }

    @Override
    public void initClient() {
    }
}
