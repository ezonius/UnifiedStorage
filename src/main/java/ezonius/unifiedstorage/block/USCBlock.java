package ezonius.unifiedstorage.block;

import ezonius.unifiedstorage.block.entity.USCBlockEntity;
import ezonius.unifiedstorage.modules.USCModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.container.Container;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class USCBlock extends BlockWithEntity {
    public static final Identifier ID = new Identifier(USCModule.MODGROUP, USCModule.USC_ID);
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    public static final Identifier CONTENTS = new Identifier("contents");
    private final DyeColor color = USCModule.USC_COLOR;

    public USCBlock() {
        super(FabricBlockSettings.of(Material.SHULKER_BOX).strength(2.0F, 0.0F).dynamicBounds().build());
        setDefaultState(getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new USCBlockEntity(color);
    }

    @Override
    public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof USCBlockEntity) {
                Direction direction = blockState.get(FACING);
                USCBlockEntity uscBlockEntity = (USCBlockEntity)blockEntity;
                boolean bl2;
                if (uscBlockEntity.getAnimationStage() == USCBlockEntity.AnimationStage.CLOSED) {
                    Box box = VoxelShapes.fullCube().getBoundingBox()
                            .stretch(0.5F * (float)direction.getOffsetX(),
                                    0.5F * (float)direction.getOffsetY(),
                                    0.5F * (float)direction.getOffsetZ())
                            .shrink(direction.getOffsetX(),
                                    direction.getOffsetY(),
                                    direction.getOffsetZ());
                    bl2 = world.doesNotCollide(box.offset(blockPos.offset(direction)));
                } else {
                    bl2 = true;
                }

                if (bl2) {
                    ContainerProviderRegistry.INSTANCE.openContainer(USCBlock.ID, player, (buf) -> buf.writeBlockPos(blockPos));
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof USCBlockEntity) {
            USCBlockEntity uscBlockEntity = (USCBlockEntity) blockEntity;
            if (!world.isClient && player.isCreative() && !uscBlockEntity.isInvEmpty()) {
                ItemStack itemStack = getItemStack(this.getColor());
                CompoundTag compoundTag = uscBlockEntity.serializeInventory(new CompoundTag());
                if (!compoundTag.isEmpty()) {
                    itemStack.putSubTag("BlockEntityTag", compoundTag);
                }

                if (uscBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(uscBlockEntity.getCustomName());
                }

                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                uscBlockEntity.checkLootInteraction(player);
            }
            super.onBreak(world, pos, state, player);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof USCBlockEntity) {
            USCBlockEntity uscBlockEntity = (USCBlockEntity)blockEntity;
            builder = builder.putDrop(CONTENTS, (lootContext, consumer) -> {
                for(int i = 0; i < uscBlockEntity.getInvSize(); ++i) {
                    consumer.accept(uscBlockEntity.getInvStack(i));
                }

            });
        }

        return super.getDroppedStacks(state, builder);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof USCBlockEntity) {
                ((USCBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof USCBlockEntity) {
                world.updateHorizontalAdjacent(pos, state.getBlock());
            }

            super.onBlockRemoved(state, world, pos, newState, moved);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(ItemStack stack, BlockView view, List<Text> tooltip, TooltipContext options) {
        super.buildTooltip(stack, view, tooltip, options);
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag != null) {
            if (compoundTag.contains("LootTable", 8)) {
                tooltip.add(new LiteralText("???????"));
            }

            if (compoundTag.contains("Items", 9)) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(108, ItemStack.EMPTY);
                Inventories.fromTag(compoundTag, defaultedList);
                int i = 0;
                int j = 0;

                for (ItemStack itemStack : defaultedList) {
                    if (!itemStack.isEmpty()) {
                        ++j;
                        if (i <= 4) {
                            ++i;
                            Text text = itemStack.getName().deepCopy();
                            text.append(" x").append(String.valueOf(itemStack.getCount()));
                            tooltip.add(text);
                        }
                    }
                }

                if (j - i > 0) {
                    tooltip.add((new TranslatableText("container.unifiedstoragecontroller.more", j - i)).formatted(Formatting.ITALIC));
                }
            }
        }

    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
        BlockEntity blockEntity = view.getBlockEntity(pos);
        return blockEntity instanceof USCBlockEntity ? VoxelShapes.cuboid(((USCBlockEntity)blockEntity).getBoundingBox(state)) : VoxelShapes.fullCube();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Container.calculateComparatorOutput((Inventory)world.getBlockEntity(pos));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        USCBlockEntity uscBlockEntity = (USCBlockEntity)world.getBlockEntity(pos);
        CompoundTag compoundTag = Objects.requireNonNull(uscBlockEntity).serializeInventory(new CompoundTag());
        if (!compoundTag.isEmpty()) {
            itemStack.putSubTag("BlockEntityTag", compoundTag);
        }

        return itemStack;
    }

    @Environment(EnvType.CLIENT)
    public static DyeColor getColor(Item item) {
        return getColor(Block.getBlockFromItem(item));
    }

    @Environment(EnvType.CLIENT)
    public static DyeColor getColor(Block block) {
        return block instanceof USCBlock ? ((USCBlock)block).getColor() : null;
    }

    public static Block get(DyeColor dyeColor) {
        return USCModule.USC_BLOCK;
//        if (dyeColor == null) {
//            return Blocks.SHULKER_BOX;
//        } else {
//            switch(dyeColor) {
//                case WHITE:
//                    return Blocks.WHITE_SHULKER_BOX;
//                case ORANGE:
//                    return Blocks.ORANGE_SHULKER_BOX;
//                case MAGENTA:
//                    return Blocks.MAGENTA_SHULKER_BOX;
//                case LIGHT_BLUE:
//                    return Blocks.LIGHT_BLUE_SHULKER_BOX;
//                case YELLOW:
//                    return Blocks.YELLOW_SHULKER_BOX;
//                case LIME:
//                    return Blocks.LIME_SHULKER_BOX;
//                case PINK:
//                    return Blocks.PINK_SHULKER_BOX;
//                case GRAY:
//                    return Blocks.GRAY_SHULKER_BOX;
//                case LIGHT_GRAY:
//                    return Blocks.LIGHT_GRAY_SHULKER_BOX;
//                case CYAN:
//                    return Blocks.CYAN_SHULKER_BOX;
//                case BLUE:
//                    return Blocks.BLUE_SHULKER_BOX;
//                case BROWN:
//                    return Blocks.BROWN_SHULKER_BOX;
//                case GREEN:
//                    return Blocks.GREEN_SHULKER_BOX;
//                case RED:
//                    return Blocks.RED_SHULKER_BOX;
//                case BLACK:
//                    return Blocks.BLACK_SHULKER_BOX;
//                case PURPLE:
//                default:
//                    return Blocks.PURPLE_SHULKER_BOX;
//            }
//        }
    }

    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getItemStack(DyeColor color) {
        return new ItemStack(get(color));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
