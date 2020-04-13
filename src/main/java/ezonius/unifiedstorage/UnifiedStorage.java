package ezonius.unifiedstorage;

import ezonius.unifiedstorage.UnifiedStorageController.USCModule;
import net.fabricmc.api.ModInitializer;

public class UnifiedStorage implements ModInitializer {

    @Override
    public void onInitialize() {
        new USCModule().initCommon();
    }
}
