package ezonius.unifiedstorage.UnifiedStorageController;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;

public class USCBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING;
    public static final Identifier CONTENTS;
    private final DyeColor color = DyeColor.BLACK;

    public USCBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    static {
        FACING = FacingBlock.FACING;
        CONTENTS = new Identifier("contents");
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new USCBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return ActionResult.SUCCESS;
        Inventory blockEntity = (Inventory) world.getBlockEntity(blockPos);


        if (!player.getStackInHand(hand).isEmpty()) {
            // Check what is the first open slot and put an item from the player's hand there
            if (Objects.requireNonNull(blockEntity).getInvStack(0).isEmpty()) {
                // Put the stack the player is holding into the inventory
                blockEntity.setInvStack(0, player.getStackInHand(hand).copy());
                // Remove the stack from the player's hand
                player.getStackInHand(hand).setCount(0);
            } else if (blockEntity.getInvStack(1).isEmpty()) {
                blockEntity.setInvStack(1, player.getStackInHand(hand).copy());
                player.getStackInHand(hand).setCount(0);
            } else {
                // If the player is not holding anything we'll get give him the items in the block entity one by one

                // Find the first slot that has an item and give it to the player
                if (!blockEntity.getInvStack(1).isEmpty()) {
                    // Give the player the stack in the inventory
                    player.inventory.offerOrDrop(world, blockEntity.getInvStack(1));
                    // Remove the stack from the inventory
                    blockEntity.removeInvStack(1);
                } else if (!blockEntity.getInvStack(0).isEmpty()) {
                    player.inventory.offerOrDrop(world, blockEntity.getInvStack(0));
                    blockEntity.removeInvStack(0);
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}
