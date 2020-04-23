package ezonius.unifiedstorage;

import ezonius.unifiedstorage.block.EnhBarrelBlock;
import ezonius.unifiedstorage.block.StorageInterfaceBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;

public class UnifiedStorage {
    public static String MODNAME = "unifiedstorage";
    public static String EB_BLOCKID_BASE = "enhanced_barrel";
    public static String SI_BLOCKID_BASE = "storage_interface";
    public static int MAX_STACK_SIZE = 64;
    public static int ROW_LENGTH = 9;
    public static int MAX_ROWS = 6;

    // Block Identifiers
    public static Identifier EB_BLOCKID_WOOD = UnifiedStorage.ModIdentifier(EB_BLOCKID_BASE + "_wood");
    public static Identifier EB_BLOCKID_IRON = UnifiedStorage.ModIdentifier(EB_BLOCKID_BASE + "_iron");
    public static Identifier EB_BLOCKID_GOLD = UnifiedStorage.ModIdentifier(EB_BLOCKID_BASE + "_gold");
    public static Identifier EB_BLOCKID_DIAMOND = UnifiedStorage.ModIdentifier(EB_BLOCKID_BASE + "_diamond");
    public static Identifier EB_BLOCKID_EMERALD = UnifiedStorage.ModIdentifier(EB_BLOCKID_BASE + "_emerald");
    public static Identifier SI_BLOCKID = UnifiedStorage.ModIdentifier(SI_BLOCKID_BASE);

    // Inventory Sizes
    public static final int EB_BLOCK_WOOD_INVSIZE = 3 * ROW_LENGTH;
    public static final int EB_BLOCK_IRON_INVSIZE = 6 * ROW_LENGTH;
    public static final int EB_BLOCK_GOLD_INVSIZE = 9 * ROW_LENGTH;
    public static final int EB_BLOCK_DIAMOND_INVSIZE = 12 * ROW_LENGTH;
    public static final int EB_BLOCK_EMERALD_INVSIZE = 15 * ROW_LENGTH;

    // Blocks
    public static final Block EB_BLOCK_WOOD =      new EnhBarrelBlock(FabricBlockSettings.of(Material.WOOD).build(), UnifiedStorage.EB_BLOCKID_WOOD, EB_BLOCK_WOOD_INVSIZE);
    public static final Block EB_BLOCK_IRON =      new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.EB_BLOCKID_IRON, EB_BLOCK_IRON_INVSIZE);
    public static final Block EB_BLOCK_GOLD =      new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.EB_BLOCKID_GOLD, EB_BLOCK_GOLD_INVSIZE);
    public static final Block EB_BLOCK_DIAMOND =   new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.EB_BLOCKID_DIAMOND, EB_BLOCK_DIAMOND_INVSIZE);
    public static final Block EB_BLOCK_EMERALD =   new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.EB_BLOCKID_EMERALD, EB_BLOCK_EMERALD_INVSIZE);
    public static final Block SI_BLOCK =           new StorageInterfaceBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.SI_BLOCKID);


    public static Identifier ModIdentifier(String blockId) {
        return new Identifier(UnifiedStorage.MODNAME, blockId);
    }

    public static ArrayList<Identifier> getModIdentifiers() {
        return new ArrayList<>(Arrays.asList(
                EB_BLOCKID_WOOD,
                EB_BLOCKID_IRON,
                EB_BLOCKID_GOLD,
                EB_BLOCKID_DIAMOND,
                EB_BLOCKID_EMERALD
        ));
    }
}
