package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.client.gui.screen.ingame.USCScreen;
import ezonius.unifiedstorage.block.USCBlock;
import ezonius.unifiedstorage.block.entity.USCBlockEntity;
import ezonius.unifiedstorage.client.render.block.entity.USCBlockEntityRenderer;
import ezonius.unifiedstorage.client.render.entity.model.USCEntityModel;
import ezonius.unifiedstorage.container.USCContainer;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class USCModule implements InitModule {
    public static final String MODGROUP = UnifiedStorage.MODNAME;
    public static final String USC_ID = "unifiedstoragecontroller";
    public static final DyeColor USC_COLOR = DyeColor.BLACK;
    public static final Block USC_BLOCK = new USCBlock();
    public static final BlockItem USC_ITEM = new BlockItem(USC_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS));

    public static BlockEntityType<USCBlockEntity> USC_BLOCK_ENTITY_TYPE;
    public static final String USC_CONTAINER_TRANSLATION_KEY = Util.createTranslationKey("container", USCBlock.ID);

    @Override
    public void initCommon() {
        Registry.register(Registry.BLOCK, USCBlock.ID, USC_BLOCK);
        Registry.register(Registry.ITEM, USCBlock.ID, USC_ITEM);
        USC_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                USCBlock.ID,
                BlockEntityType.Builder.create(() -> new USCBlockEntity(USC_COLOR),
                        USC_BLOCK).build(null));

        ContainerProviderRegistry.INSTANCE.registerFactory(USCBlock.ID, (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final BlockPos pos = buf.readBlockPos();
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();
            return Objects.requireNonNull(world.getBlockState(pos)
                    .createContainerFactory(player.world, pos))
                    .createMenu(syncId, player.inventory, player);

        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        ScreenProviderRegistry.INSTANCE.<USCContainer>registerFactory(USCBlock.ID, (container) -> new USCScreen(container, MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.inventory : null, new TranslatableText(USC_CONTAINER_TRANSLATION_KEY)));
        BlockEntityRendererRegistry.INSTANCE.register(USC_BLOCK_ENTITY_TYPE, (dispatcher) -> new USCBlockEntityRenderer(new USCEntityModel<>(), dispatcher));
    }
}
