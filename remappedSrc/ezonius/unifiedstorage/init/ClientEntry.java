package ezonius.unifiedstorage.init;

import ezonius.unifiedstorage.modules.EnhBarrelModuleClient;
import ezonius.unifiedstorage.modules.StorageInterfaceModuleClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientEntry implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new EnhBarrelModuleClient().initClient();
        new StorageInterfaceModuleClient().initClient();
    }
}
