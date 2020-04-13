package ezonius.unifiedstorage;

import ezonius.unifiedstorage.modules.USCModule;
import net.fabricmc.api.ModInitializer;

public class UnifiedStorage implements ModInitializer {

    @Override
    public void onInitialize() {
        new USCModule().initCommon();
    }
}
