package ezonius.unifiedstorage;

import ezonius.unifiedstorage.UnifiedStorageController.USCBlock;
import ezonius.unifiedstorage.UnifiedStorageController.USCBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class UnifiedStorage implements ModInitializer {
    public static final String MODGROUP = "unifiedstorage";
    public static final Block UNIFIED_STORAGE_CONTROLLER_BLOCK = new USCBlock(FabricBlockSettings.of(Material.SHULKER_BOX, MaterialColor.PURPLE_TERRACOTTA).build());
    public static final BlockItem UNIFIED_STORAGE_CONTROLLER_ITEM = new BlockItem(UNIFIED_STORAGE_CONTROLLER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
    public static BlockEntityType<USCBlockEntity> UNIFIED_STORAGE_CONTROLLER_BLOCK_ENTITY;
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MODGROUP, "unifiedstoragecontroller"), UNIFIED_STORAGE_CONTROLLER_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MODGROUP, "unifiedstoragecontroller"), UNIFIED_STORAGE_CONTROLLER_ITEM);
        UNIFIED_STORAGE_CONTROLLER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                MODGROUP + ":unifiedstoragecontroller",
                BlockEntityType.Builder.create(() -> new USCBlockEntity(),
                        UNIFIED_STORAGE_CONTROLLER_BLOCK).build(null));
    }
}
