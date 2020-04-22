package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.STBlock;
import ezonius.unifiedstorage.block.entity.STBlockEntity;
import ezonius.unifiedstorage.client.gui.screen.ingame.STBlockController;
import ezonius.unifiedstorage.client.gui.screen.ingame.STScreen;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.BlockContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.HashSet;

public class STModule implements InitModule {
    public static HashMap<Block, BlockEntityType<STBlockEntity>> ST_BLOCK_ENTITY_TYPES = new HashMap<>();

    @Override
    public void initCommon() {
        RegisterSTBlock(UnifiedStorage.ST_BLOCKID_WOOD, UnifiedStorage.ST_BLOCK_WOOD, UnifiedStorage.ST_BLOCK_WOOD_INVSIZE);
        RegisterSTBlock(UnifiedStorage.ST_BLOCKID_IRON, UnifiedStorage.ST_BLOCK_IRON, UnifiedStorage.ST_BLOCK_IRON_INVSIZE);
        RegisterSTBlock(UnifiedStorage.ST_BLOCKID_DIAMOND, UnifiedStorage.ST_BLOCK_DIAMOND, UnifiedStorage.ST_BLOCK_DIAMOND_INVSIZE);
    }

    public static void RegisterSTBlock(Identifier blockId, Block block, int invSize) {
        Registry.register(Registry.BLOCK, blockId, block);
        ST_BLOCK_ENTITY_TYPES.put(block, Registry.register(Registry.BLOCK_ENTITY_TYPE, blockId,
                BlockEntityType.Builder.create((() -> new STBlockEntity(block, invSize)), block).build(null)));
        ContainerProviderRegistry.INSTANCE.registerFactory(blockId,
                (syncId, identifier, player, packetByteBuf) ->
                        new STBlockController(syncId, player.inventory, BlockContext.create(player.world, packetByteBuf.readBlockPos()), invSize, false));
        Registry.register(Registry.ITEM, blockId, new BlockItem(block, new Item.Settings().group(ItemGroup.INVENTORY)));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
    }
}
