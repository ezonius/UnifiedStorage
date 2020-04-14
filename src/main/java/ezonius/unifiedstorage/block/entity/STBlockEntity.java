package ezonius.unifiedstorage.block.entity;

import ezonius.unifiedstorage.block.STBlock;
import ezonius.unifiedstorage.inventory.ImplementedInventory;
import ezonius.unifiedstorage.modules.STModule;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;

public class STBlockEntity extends LootableContainerBlockEntity implements ImplementedInventory {

    private DefaultedList<ItemStack> inventory;
    private int viewerCount;

    public STBlockEntity() {
        super(STModule.ST_BLOCK_ENTITY_TYPE);
        this.inventory = DefaultedList.ofSize(108, ItemStack.EMPTY);
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }

        return tag;
    }

    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }

    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public int getInvSize() {
        return 108;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    protected Text getContainerName() {
        return new TranslatableText("container.storageterminal");
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return null;
    }

    public void onInvOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }

            ++this.viewerCount;
            BlockState blockState = this.getCachedState();
            boolean bl = blockState.get(BarrelBlock.OPEN);
            if (!bl) {
                this.playSound(blockState, SoundEvents.BLOCK_BARREL_OPEN);
                this.setOpen(blockState, true);
            }

            this.scheduleUpdate();
        }

    }

    private void scheduleUpdate() {
        Objects.requireNonNull(this.world).getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
    }

    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        this.viewerCount = ChestBlockEntity.countViewers(Objects.requireNonNull(this.world), this, i, j, k);
        if (this.viewerCount > 0) {
            this.scheduleUpdate();
        } else {
            BlockState blockState = this.getCachedState();
            if (blockState.getBlock() != Blocks.BARREL) {
                this.markRemoved();
                return;
            }

            boolean bl = blockState.get(STBlock.OPEN);
            if (bl) {
                this.playSound(blockState, SoundEvents.BLOCK_BARREL_CLOSE);
                this.setOpen(blockState, false);
            }
        }

    }

    public void onInvClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }

    }

    private void setOpen(BlockState state, boolean open) {
        Objects.requireNonNull(this.world).setBlockState(this.getPos(), state.with(STBlock.OPEN, open), 3);
    }

    private void playSound(BlockState blockState, SoundEvent soundEvent) {
        Vec3i vec3i = blockState.get(STBlock.FACING).getVector();
        double d = (double)this.pos.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double e = (double)this.pos.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double f = (double)this.pos.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        Objects.requireNonNull(this.world).playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return pos.isWithinDistance(player.getBlockPos(), 4.5);
    }
}
