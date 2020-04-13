package ezonius.unifiedstorage.UnifiedStorageController;

import ezonius.unifiedstorage.UnifiedStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.Container;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class USCBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable {
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 54).toArray();
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private AnimationStage animationStage;
    private float animationProgress;
    private float prevAnimationProgress;
    private DyeColor cachedColor;
    private boolean cachedColorUpdateNeeded;

    public USCBlockEntity(DyeColor dyecolor) {
        super(USCModule.USC_BLOCK_ENTITY);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
        this.animationStage = USCBlockEntity.AnimationStage.CLOSED;
        this.cachedColor = dyecolor;
    }

    public USCBlockEntity() {
        this(null);
        this.cachedColorUpdateNeeded = true;
    }

    public void tick() {
        this.updateAnimation();
        if (this.animationStage == USCBlockEntity.AnimationStage.OPENING || this.animationStage == USCBlockEntity.AnimationStage.CLOSING) {
            this.pushEntities();
        }
    }

    protected void updateAnimation() {
        this.prevAnimationProgress = this.animationProgress;
        switch (this.animationStage) {
            case CLOSED:
                this.animationProgress = 0.0F;
                break;
            case OPENING:
                this.animationProgress += 0.1F;
                if (this.animationProgress >= 1.0F) {
                    this.pushEntities();
                    this.animationStage = USCBlockEntity.AnimationStage.OPENED;
                    this.animationProgress = 1.0F;
                    this.updateNeighborStates();
                }
                break;
            case CLOSING:
                this.animationProgress -= 0.1F;
                if (this.animationProgress <= 0.0F) {
                    this.animationStage = USCBlockEntity.AnimationStage.CLOSED;
                    this.animationProgress = 0.0F;
                    this.updateNeighborStates();
                }
                break;
            case OPENED:
                this.animationProgress = 1.0F;
        }

    }

    public USCBlockEntity.AnimationStage getAnimationStage() {
        return this.animationStage;
    }

    public Box getBoundingBox(BlockState state) {
        return this.getBoundingBox(state.get(USCBlock.FACING));
    }

    public Box getBoundingBox(Direction openDirection) {
        float f = this.getAnimationProgress(1.0F);
        return VoxelShapes.fullCube().getBoundingBox().stretch(0.5F * f * (float) openDirection.getOffsetX(), 0.5F * f * (float) openDirection.getOffsetY(), 0.5F * f * (float) openDirection.getOffsetZ());
    }

    private Box getCollisionBox(Direction facing) {
        Direction direction = facing.getOpposite();
        return this.getBoundingBox(facing).shrink(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    private void pushEntities() {
        BlockState blockState = Objects.requireNonNull(this.world).getBlockState(this.getPos());
        if (blockState.getBlock() instanceof USCBlock) {
            Direction direction = blockState.get(USCBlock.FACING);
            Box box = this.getCollisionBox(direction).offset(this.pos);
            List<Entity> list = this.world.getEntities(null, box);
            if (!list.isEmpty()) {
                for (Entity entity : list) {
                    if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                        double d = 0.0D;
                        double e = 0.0D;
                        double f = 0.0D;
                        Box box2 = entity.getBoundingBox();
                        switch (direction.getAxis()) {
                            case X:
                                if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
                                    d = box.x2 - box2.x1;
                                } else {
                                    d = box2.x2 - box.x1;
                                }

                                d += 0.01D;
                                break;
                            case Y:
                                if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
                                    e = box.y2 - box2.y1;
                                } else {
                                    e = box2.y2 - box.y1;
                                }

                                e += 0.01D;
                                break;
                            case Z:
                                if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
                                    f = box.z2 - box2.z1;
                                } else {
                                    f = box2.z2 - box.z1;
                                }

                                f += 0.01D;
                        }

                        entity.move(MovementType.SHULKER_BOX, new Vec3d(d * (double) direction.getOffsetX(), e * (double) direction.getOffsetY(), f * (double) direction.getOffsetZ()));
                    }
                }

            }
        }
    }

    public int getInvSize() {
        return this.inventory.size();
    }

    public boolean onBlockAction(int i, int j) {
        if (i == 1) {
            this.viewerCount = j;
            if (j == 0) {
                this.animationStage = USCBlockEntity.AnimationStage.CLOSING;
                this.updateNeighborStates();
            }

            if (j == 1) {
                this.animationStage = USCBlockEntity.AnimationStage.OPENING;
                this.updateNeighborStates();
            }

            return true;
        } else {
            return super.onBlockAction(i, j);
        }
    }

    private void updateNeighborStates() {
        this.getCachedState().updateNeighborStates(this.getWorld(), this.getPos(), 3);
    }

    public void onInvOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }

            ++this.viewerCount;
            Objects.requireNonNull(this.world).addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount == 1) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public void onInvClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            Objects.requireNonNull(this.world).addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount <= 0) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    protected Text getContainerName() {
        return new TranslatableText("container.unifiedstoragecontroller");
    }

    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.deserializeInventory(tag);
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        return this.serializeInventory(tag);
    }

    public void deserializeInventory(CompoundTag tag) {
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag) && tag.contains("Items", 9)) {
            Inventories.fromTag(tag, this.inventory);
        }

    }

    public CompoundTag serializeInventory(CompoundTag tag) {
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory, false);
        }

        return tag;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    public boolean isInvEmpty() {
        Iterator<ItemStack> var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while (itemStack.isEmpty());

        return false;
    }

    public int[] getInvAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof USCBlock);
    }

    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public float getAnimationProgress(float f) {
        return MathHelper.lerp(f, this.prevAnimationProgress, this.animationProgress);
    }

    @Environment(EnvType.CLIENT)
    public DyeColor getColor() {
        if (this.cachedColorUpdateNeeded) {
            this.cachedColor = USCBlock.getColor(this.getCachedState().getBlock());
            this.cachedColorUpdateNeeded = false;
        }
        return this.cachedColor;
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        //return new ShulkerBoxContainer(i, playerInventory, this);
        return new USCContainer(i, playerInventory, this);
    }

    public enum AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING
    }
}
