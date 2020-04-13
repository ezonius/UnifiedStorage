package ezonius.unifiedstorage;

import ezonius.unifiedstorage.UnifiedStorageController.USCModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UnifiedStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new USCModule().initClient();
    }
}
