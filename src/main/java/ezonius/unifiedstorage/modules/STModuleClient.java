package ezonius.unifiedstorage.modules;

import ezonius.unifiedstorage.block.STBlock;
import ezonius.unifiedstorage.client.gui.screen.ingame.STBlockController;
import ezonius.unifiedstorage.client.gui.screen.ingame.STScreen;
import ezonius.unifiedstorage.init.InitModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;

@Environment(EnvType.CLIENT)
public class STModuleClient implements InitModule {
    @Override
    public void initCommon() {

    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(STBlock.ID,
                (i, identifier, player, packetByteBuf) -> new STScreen(new STBlockController(i,
                        player.inventory,
                        BlockContext.create(player.world, packetByteBuf.readBlockPos())), player));
    }
}
