package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.client.gui.screen.ingame.ScrollableContainer;
import ezonius.unifiedstorage.client.gui.screen.ingame.ScrollableScreen;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

public class StorageInterfaceModuleClient implements InitModule {
    @Override
    public void initCommon() {
    }

    @Override
    public void initClient() {
        RegisterScreen(UnifiedStorage.SI_BLOCKID, 0);
    }

    public static void RegisterScreen(Identifier blockId, int invSize) {
        ScreenProviderRegistry.INSTANCE.registerFactory(blockId,
                (i, identifier, player, packetByteBuf) -> new ScrollableScreen(new ScrollableContainer(i,
                        player.inventory,
                        BlockContext.create(player.world, packetByteBuf.readBlockPos()), invSize, false), player));
    }
}
