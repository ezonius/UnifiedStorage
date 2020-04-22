package ezonius.unifiedstorage;

import ezonius.unifiedstorage.block.EnhBarrelBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;

public class UnifiedStorage {
    public static String MODNAME = "unifiedstorage";
    public static String ST_BLOCK_ID = "enhanced_barrel";
    public static int MAX_STACK_SIZE = 64;
    public static int ROW_LENGTH = 9;
    public static int MAX_ROWS = 6;

    // Block Identifiers
    public static Identifier ST_BLOCKID_WOOD = UnifiedStorage.ModIdentifier(ST_BLOCK_ID + "_wood");
    public static Identifier ST_BLOCKID_IRON = UnifiedStorage.ModIdentifier(ST_BLOCK_ID + "_iron");
    public static Identifier ST_BLOCKID_GOLD = UnifiedStorage.ModIdentifier(ST_BLOCK_ID + "_gold");
    public static Identifier ST_BLOCKID_DIAMOND = UnifiedStorage.ModIdentifier(ST_BLOCK_ID + "_diamond");
    public static Identifier ST_BLOCKID_EMERALD = UnifiedStorage.ModIdentifier(ST_BLOCK_ID + "_emerald");

    // Inventory Sizes
    public static final int ST_BLOCK_WOOD_INVSIZE = 3 * ROW_LENGTH;
    public static final int ST_BLOCK_IRON_INVSIZE = 6 * ROW_LENGTH;
    public static final int ST_BLOCK_GOLD_INVSIZE = 9 * ROW_LENGTH;
    public static final int ST_BLOCK_DIAMOND_INVSIZE = 12 * ROW_LENGTH;
    public static final int ST_BLOCK_EMERALD_INVSIZE = 15 * ROW_LENGTH;

    // Blocks
    public static final Block ST_BLOCK_WOOD =      new EnhBarrelBlock(FabricBlockSettings.of(Material.WOOD).build(), UnifiedStorage.ST_BLOCKID_WOOD, ST_BLOCK_WOOD_INVSIZE);
    public static final Block ST_BLOCK_IRON =      new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.ST_BLOCKID_IRON, ST_BLOCK_IRON_INVSIZE);
    public static final Block ST_BLOCK_GOLD =      new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.ST_BLOCKID_GOLD, ST_BLOCK_GOLD_INVSIZE);
    public static final Block ST_BLOCK_DIAMOND =   new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.ST_BLOCKID_DIAMOND, ST_BLOCK_DIAMOND_INVSIZE);
    public static final Block ST_BLOCK_EMERALD =   new EnhBarrelBlock(FabricBlockSettings.of(Material.METAL).build(), UnifiedStorage.ST_BLOCKID_EMERALD, ST_BLOCK_EMERALD_INVSIZE);


    public static Identifier ModIdentifier(String blockId) {
        return new Identifier(UnifiedStorage.MODNAME, blockId);
    }

    public static ArrayList<Identifier> getModIdentifiers() {
        return new ArrayList<>(Arrays.asList(
                ST_BLOCKID_WOOD,
                ST_BLOCKID_IRON,
                ST_BLOCKID_GOLD,
                ST_BLOCKID_DIAMOND,
                ST_BLOCKID_EMERALD
        ));
    }
}
