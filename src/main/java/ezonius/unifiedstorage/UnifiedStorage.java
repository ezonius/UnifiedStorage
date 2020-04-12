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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class UnifiedStorage implements ModInitializer {
    public static final String MODGROUP = "unifiedstorage";
    public static final String USC_ID = "unifiedstoragecontroller";
    public static final Block USC_BLOCK = new USCBlock();
    public static final BlockItem USC_ITEM = new BlockItem(USC_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
    public static BlockEntityType<USCBlockEntity> USC_BLOCK_ENTITY;
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MODGROUP, USC_ID), USC_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MODGROUP, USC_ID), USC_ITEM);
        USC_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                MODGROUP + ":" + USC_ID,
                BlockEntityType.Builder.create(USCBlockEntity::new,
                        USC_BLOCK).build(null));
    }
}
