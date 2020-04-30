package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.entity.StorageTerminalBlockEntity;
import ezonius.unifiedstorage.container.ScrollableContainer;
import ezonius.unifiedstorage.client.gui.screen.ingame.ScrollableScreen;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;

public class StorageInterfaceModuleClient implements InitModule {
    @Override
    public void initCommon() {
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void initClient() {
        RegisterScreen(UnifiedStorage.SI_BLOCKID, 0);
    }

    @Environment(EnvType.CLIENT)
    public static void RegisterScreen(Identifier blockId, int invSize) {
        ScreenProviderRegistry.INSTANCE.registerFactory(blockId,
                (i, identifier, player, packetByteBuf) -> new ScrollableScreen(new ScrollableContainer(i,
                        player.inventory,
                        ScreenHandlerContext.create(player.world, packetByteBuf.readBlockPos()), invSize, false), player));
    }
}
