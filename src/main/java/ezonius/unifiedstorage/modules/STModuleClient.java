package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.STBlock;
import ezonius.unifiedstorage.client.gui.screen.ingame.STBlockController;
import ezonius.unifiedstorage.client.gui.screen.ingame.STScreen;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class STModuleClient implements InitModule {
    @Override
    public void initCommon() {

    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        RegisterScreen(UnifiedStorage.ST_BLOCKID_WOOD, UnifiedStorage.ST_BLOCK_WOOD_INVSIZE);
    }

    public static void RegisterScreen(Identifier blockId, int invSize) {
        ScreenProviderRegistry.INSTANCE.registerFactory(blockId,
                (i, identifier, player, packetByteBuf) -> new STScreen(new STBlockController(i,
                        player.inventory,
                        BlockContext.create(player.world, packetByteBuf.readBlockPos()), invSize), player));
    }
}
