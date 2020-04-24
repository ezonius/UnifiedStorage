package ezonius.unifiedstorage.block;

import blue.endless.jankson.annotation.Nullable;
import ezonius.unifiedstorage.block.entity.EnhBarrelBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class EnhBarrelBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;
    private final Identifier blockId;
    private final int invSize;

    public EnhBarrelBlock(Settings settings, Identifier blockId, int InvSize) {
        super(settings);
        this.blockId = blockId;
        invSize = InvSize;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;
        else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EnhBarrelBlockEntity) {
                ContainerProviderRegistry.INSTANCE.openContainer(blockId, player, (packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onBlockRemoved(state, world, pos, newState, moved);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnhBarrelBlockEntity) {
            ((EnhBarrelBlockEntity)blockEntity).tick();
        }
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView view) {
        return new EnhBarrelBlockEntity(this, this.invSize);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (itemStack.hasCustomName()) {
            if (blockEntity instanceof EnhBarrelBlockEntity) {
                ((EnhBarrelBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
}
