package ezonius.unifiedstorage.init;

import ezonius.unifiedstorage.modules.EnhBarrelModule;
import net.fabricmc.api.ModInitializer;

public class CommonEntry implements ModInitializer {

    @Override
    public void onInitialize() {
        new EnhBarrelModule().initCommon();
    }
}
