package ezonius.unifiedstorage.mixins;

import ezonius.unifiedstorage.misc.Utils;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
    @Inject(at = @At("TAIL"), method = "onPlaced")
    public void onPlacedDo(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo info) {
        Utils.updateAllLinkedStorageInterfaceBlockEntity(new HashSet<>(), world, pos);
    }

    @Inject(at = @At("TAIL"), method = "onBlockRemoved")
    public void onBlockRemovedDo(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo info) {
        Utils.updateAllLinkedStorageInterfaceBlockEntity(new HashSet<>(), world, pos);
    }
}
