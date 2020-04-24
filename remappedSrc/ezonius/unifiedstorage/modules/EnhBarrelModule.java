package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
import ezonius.unifiedstorage.container.ScrollableContainer;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class EnhBarrelModule implements InitModule {
    public static HashMap<Block, BlockEntityType<EnhBarrelBlockEntity>> ST_BLOCK_ENTITY_TYPES = new HashMap<>();

    @Override
    public void initCommon() {
        RegisterSTBlock(UnifiedStorage.EB_BLOCKID_WOOD, UnifiedStorage.EB_BLOCK_WOOD, UnifiedStorage.EB_BLOCK_WOOD_INVSIZE);
        //RegisterSTBlock(UnifiedStorage.ST_BLOCKID_IRON, UnifiedStorage.ST_BLOCK_IRON, UnifiedStorage.ST_BLOCK_IRON_INVSIZE);
        //RegisterSTBlock(UnifiedStorage.ST_BLOCKID_DIAMOND, UnifiedStorage.ST_BLOCK_DIAMOND, UnifiedStorage.ST_BLOCK_DIAMOND_INVSIZE);
    }

    public static void RegisterSTBlock(Identifier blockId, Block block, int invSize) {
        Registry.register(Registry.BLOCK, blockId, block);
        ST_BLOCK_ENTITY_TYPES.put(block, Registry.register(Registry.BLOCK_ENTITY_TYPE, blockId,
                BlockEntityType.Builder.create((() -> new EnhBarrelBlockEntity(block, invSize)), block).build(null)));
        ContainerProviderRegistry.INSTANCE.registerFactory(blockId,
                (syncId, identifier, player, packetByteBuf) ->
                        new ScrollableContainer(syncId, player.inventory, ScreenHandlerContext.create(player.world, packetByteBuf.readBlockPos()), invSize, false));
        Registry.register(Registry.ITEM, blockId, new BlockItem(block, new Item.Settings().group(ItemGroup.INVENTORY)));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
    }
}
