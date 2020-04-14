package ezonius.unifiedstorage.client.gui.screen.ingame;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class STScreen extends CottonInventoryScreen<STBlockController> {
    public STScreen(STBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
