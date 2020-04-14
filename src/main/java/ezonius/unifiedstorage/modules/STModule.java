package ezonius.unifiedstorage.modules;

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
import net.minecraft.util.registry.Registry;

public class STModule implements InitModule {
    public static final Block ST_BLOCK = new STBlock(FabricBlockSettings.of(Material.WOOD).build());
    public static BlockEntityType<STBlockEntity> ST_BLOCK_ENTITY_TYPE;

    @Override
    public void initCommon() {

        Registry.register(Registry.BLOCK, STBlock.ID, ST_BLOCK);
        ST_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, STBlock.ID,
                BlockEntityType.Builder.create((STBlockEntity::new), ST_BLOCK).build(null));
        ContainerProviderRegistry.INSTANCE.registerFactory(STBlock.ID,
                (syncId, identifier, playerEntity, packetByteBuf) ->
                        new STBlockController(syncId, playerEntity.inventory, BlockContext.create(playerEntity.world, packetByteBuf.readBlockPos())));
        Registry.register(Registry.ITEM, STBlock.ID, new BlockItem(ST_BLOCK, new Item.Settings().group(ItemGroup.INVENTORY)));


    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(STBlock.ID,
                (i, identifier, playerEntity, packetByteBuf) -> new STScreen(new STBlockController(i,
                        playerEntity.inventory,
                        BlockContext.create(playerEntity.world, packetByteBuf.readBlockPos())), playerEntity));
    }
}
