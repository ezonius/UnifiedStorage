package ezonius.unifiedstorage;

import ezonius.unifiedstorage.UnifiedStorageController.USCBlock;
import ezonius.unifiedstorage.UnifiedStorageController.USCBlockEntity;
import ezonius.unifiedstorage.UnifiedStorageController.USCContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class UnifiedStorage implements ModInitializer {
    public static final String MODGROUP = "unifiedstorage";
    public static final String USC_ID = "unifiedstoragecontroller";
    public static final DyeColor USC_COLOR = DyeColor.BLACK;
    public static final Block USC_BLOCK = new USCBlock();
    public static final BlockItem USC_ITEM = new BlockItem(USC_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));

    public static BlockEntityType<USCBlockEntity> USC_BLOCK_ENTITY;
    public static final String USC_CONTAINER_TRANSLATION_KEY = Util.createTranslationKey("container", USCBlock.ID);

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, USCBlock.ID, USC_BLOCK);
        Registry.register(Registry.ITEM, USCBlock.ID, USC_ITEM);
        USC_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                MODGROUP + ":" + USC_ID,
                BlockEntityType.Builder.create(() -> new USCBlockEntity(USC_COLOR),
                        USC_BLOCK).build(null));

        ContainerProviderRegistry.INSTANCE.registerFactory(USCBlock.ID, (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final BlockPos pos = buf.readBlockPos();
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();
//            if (block instanceof InventoryProvider)
//            {
//                return new USCContainer(syncId, player.inventory, ((InventoryProvider) block).getInventory(state, world, pos));
//            }
//            return null;
            return Objects.requireNonNull(world.getBlockState(pos)
                    .createContainerFactory(player.world, pos))
                    .createMenu(syncId, player.inventory, player);

        });
    }
}
