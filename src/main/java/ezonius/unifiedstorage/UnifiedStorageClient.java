package ezonius.unifiedstorage;

import ezonius.unifiedstorage.modules.STModule;
import ezonius.unifiedstorage.modules.USCModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UnifiedStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new USCModule().initClient();
        new STModule().initClient();
    }
}
