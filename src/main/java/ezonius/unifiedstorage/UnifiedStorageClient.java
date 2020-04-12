package ezonius.unifiedstorage;

import ezonius.unifiedstorage.UnifiedStorageController.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class UnifiedStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.<USCContainer>registerFactory(USCBlock.ID, (container) -> new USCScreen(container, MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.inventory : null, new TranslatableText(UnifiedStorage.USC_CONTAINER_TRANSLATION_KEY)));
        BlockEntityRendererRegistry.INSTANCE.register(UnifiedStorage.USC_BLOCK_ENTITY, (dispatcher) -> new USCBlockEntityRenderer(new USCEntityModel<>(), dispatcher));
    }
}
