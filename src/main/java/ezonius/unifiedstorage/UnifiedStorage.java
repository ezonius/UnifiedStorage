package ezonius.unifiedstorage;

import ezonius.unifiedstorage.modules.STModule;
import net.fabricmc.api.ModInitializer;

public class UnifiedStorage implements ModInitializer {
    public static final String MODNAME = "unifiedstorage";

    @Override
    public void onInitialize() {
        new STModule().initCommon();
    }
}
