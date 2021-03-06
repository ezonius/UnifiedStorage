package ezonius.unifiedstorage.block.entity;

import ezonius.unifiedstorage.UnifiedStorage;
import ezonius.unifiedstorage.block.EnhBarrelBlock;
import ezonius.unifiedstorage.container.ScrollableContainer;
import ezonius.unifiedstorage.modules.EnhBarrelModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class EnhBarrelBlockEntity extends LootableContainerBlockEntity implements Inventory {

    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private final Block block;
    public int invSize;

    public EnhBarrelBlockEntity(Block block, int invSize) {
        super(EnhBarrelModule.ST_BLOCK_ENTITY_TYPES.get(block));
        this.block = block;
        this.invSize = invSize;
        this.inventory = DefaultedList.ofSize(this.invSize, ItemStack.EMPTY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }

    @Override
    public int getInvSize() {
        return this.invSize;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.enhanced_barrel");
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public void onInvOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            BlockState blockState = this.getCachedState();
            boolean bl = blockState.get(EnhBarrelBlock.OPEN);
            if (!bl) {
                this.playSound(blockState, SoundEvents.BLOCK_BARREL_OPEN);
                this.setOpen(blockState, true);
            }
            this.scheduleUpdate();
        }
    }

    public void scheduleUpdate() {
        Objects.requireNonNull(this.world).getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
    }

    public static int countViewers(World world, LockableContainerBlockEntity container, int ticksOpen, int x, int y) {
        int i = 0;
        float f = 5.0F;
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, new Box((float)ticksOpen - 5.0F, (float)x - 5.0F, (float)y - 5.0F, (float)(ticksOpen + 1) + 5.0F, (float)(x + 1) + 5.0F, (float)(y + 1) + 5.0F));
        Iterator<PlayerEntity> var8 = list.iterator();

        while(true) {
            Inventory inventory;
            do {
                PlayerEntity playerEntity;
                do {
                    if (!var8.hasNext()) {
                        return i;
                    }
                    playerEntity = var8.next();
                } while(!(playerEntity.container instanceof ScrollableContainer));
                inventory = ((ScrollableContainer) playerEntity.container).getInventory();
            } while(inventory != container);
            ++i;
        }
    }

    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        this.viewerCount = countViewers(Objects.requireNonNull(this.world), this, i, j, k);
        if (this.viewerCount > 0) {
            this.scheduleUpdate();
        } else {
            BlockState blockState = this.getCachedState();
            if (blockState.getBlock() != this.block) {
                this.markRemoved();
                return;
            }

            boolean bl = blockState.get(EnhBarrelBlock.OPEN);
            if (bl) {
                this.playSound(blockState, SoundEvents.BLOCK_BARREL_CLOSE);
                this.setOpen(blockState, false);
            }
        }

    }

    @Override
    public void onInvClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }
    }

    private void setOpen(BlockState state, boolean open) {
        Objects.requireNonNull(this.world).setBlockState(this.getPos(), state.with(EnhBarrelBlock.OPEN, open), 3);
    }

    private void playSound(BlockState blockState, SoundEvent soundEvent) {
        Vec3i vec3i = blockState.get(EnhBarrelBlock.FACING).getVector();
        double d = (double)this.pos.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double e = (double)this.pos.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double f = (double)this.pos.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        Objects.requireNonNull(this.world).playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public int getInvMaxStackAmount() {
        return UnifiedStorage.MAX_STACK_SIZE;
    }
}
