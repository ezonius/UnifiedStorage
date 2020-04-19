package ezonius.unifiedstorage.init;

import ezonius.unifiedstorage.modules.STModule;
import net.fabricmc.api.ModInitializer;

public class CommonEntry implements ModInitializer {

    @Override
    public void onInitialize() {
        new STModule().initCommon();
    }
}
