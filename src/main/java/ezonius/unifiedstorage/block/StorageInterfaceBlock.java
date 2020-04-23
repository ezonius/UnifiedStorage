package ezonius.unifiedstorage.block;

import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
import ezonius.unifiedstorage.block.entity.StorageInterfaceBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StorageInterfaceBlock extends BlockWithEntity {
    private final Identifier blockId;

    public StorageInterfaceBlock(Settings settings, Identifier blockid) {
        super(settings);
        this.blockId = blockid;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new StorageInterfaceBlockEntity(this);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;
        else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageInterfaceBlockEntity) {
                ContainerProviderRegistry.INSTANCE.openContainer(blockId, player, (packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
            }
        }
        return ActionResult.SUCCESS;
    }
}
