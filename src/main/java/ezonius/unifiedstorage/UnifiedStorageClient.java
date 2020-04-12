package ezonius.unifiedstorage;

import ezonius.unifiedstorage.UnifiedStorageController.USCBlock;
import ezonius.unifiedstorage.UnifiedStorageController.USCContainer;
import ezonius.unifiedstorage.UnifiedStorageController.USCScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class UnifiedStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.<USCContainer>registerFactory(USCBlock.ID, (container) -> new USCScreen(container, MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.inventory : null, new TranslatableText(UnifiedStorage.USC_CONTAINER_TRANSLATION_KEY)));
    }
}
