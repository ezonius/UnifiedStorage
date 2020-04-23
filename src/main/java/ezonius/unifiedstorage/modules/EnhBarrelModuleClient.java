package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.container.ScrollableContainer;
import ezonius.unifiedstorage.client.gui.screen.ingame.ScrollableScreen;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnhBarrelModuleClient implements InitModule {
    @Override
    public void initCommon() {

    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        RegisterScreen(UnifiedStorage.EB_BLOCKID_WOOD, UnifiedStorage.EB_BLOCK_WOOD_INVSIZE);
        //RegisterScreen(UnifiedStorage.ST_BLOCKID_IRON, UnifiedStorage.ST_BLOCK_IRON_INVSIZE);
        //RegisterScreen(UnifiedStorage.ST_BLOCKID_DIAMOND, UnifiedStorage.ST_BLOCK_DIAMOND_INVSIZE);
    }

    public static void RegisterScreen(Identifier blockId, int invSize) {
        ScreenProviderRegistry.INSTANCE.registerFactory(blockId,
                (i, identifier, player, packetByteBuf) -> new ScrollableScreen(new ScrollableContainer(i,
                        player.inventory,
                        BlockContext.create(player.world, packetByteBuf.readBlockPos()), invSize, false), player));
    }
}
