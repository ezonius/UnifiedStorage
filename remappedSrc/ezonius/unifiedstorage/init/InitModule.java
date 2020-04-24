package ezonius.unifiedstorage.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface InitModule {
    void initCommon();

    @Environment(EnvType.CLIENT)
    void initClient();
}
