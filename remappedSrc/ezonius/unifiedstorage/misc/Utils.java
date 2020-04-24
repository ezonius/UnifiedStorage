package ezonius.unifiedstorage.misc;

import ezonius.unifiedstorage.block.entity.StorageTerminalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static Stream<LootableContainerBlockEntity> getAdjacentInventories(World world, BlockPos pos) {
        ArrayList<BlockEntity> adjacentEntities = new ArrayList<>();
        if (world != null) {
            adjacentEntities.add(world.getBlockEntity(pos.down()));
            adjacentEntities.add(world.getBlockEntity(pos.up()));
            adjacentEntities.add(world.getBlockEntity(pos.north()));
            adjacentEntities.add(world.getBlockEntity(pos.south()));
            adjacentEntities.add(world.getBlockEntity(pos.east()));
            adjacentEntities.add(world.getBlockEntity(pos.west()));
        }
        return adjacentEntities.stream()
                .filter((entry) -> entry instanceof LootableContainerBlockEntity)
                .filter(entry -> !(entry instanceof HopperBlockEntity || entry instanceof DispenserBlockEntity))
                .map(entry -> ((LootableContainerBlockEntity) entry))
                .distinct();
    }

    public static Stream<LootableContainerBlockEntity> getRecursiveAdjacentEntities(HashSet<LootableContainerBlockEntity> checkList, World world, BlockPos pos) {
        ArrayList<LootableContainerBlockEntity> filteredAdjacent = Utils.getAdjacentInventories(world, pos)
                .filter(entry -> {
                    if (!checkList.contains(entry)) {
                        checkList.add(entry);
                        return true;
                    }
                    return false;
                }).collect(Collectors.toCollection(ArrayList::new));
        return Stream.concat(
                filteredAdjacent.stream(), filteredAdjacent.stream()
                        .flatMap(stBlockEntity ->
                                Utils.getRecursiveAdjacentEntities(checkList, stBlockEntity.getWorld(), stBlockEntity.getPos())));
    }

    public static Stream<StorageTerminalBlockEntity> getRecursiveAdjacentStorageInterfaces(HashSet<LootableContainerBlockEntity> checkList, World world, BlockPos pos) {
        return Utils.getRecursiveAdjacentEntities(checkList, world, pos)
                .filter(x -> x instanceof StorageTerminalBlockEntity)
                .map(x -> ((StorageTerminalBlockEntity)x));
    }

    public static void updateAllLinkedStorageInterfaceBlockEntity(HashSet<LootableContainerBlockEntity> checkList, World world, BlockPos pos) {
        Utils.getRecursiveAdjacentStorageInterfaces(checkList, world, pos).forEach(x -> x.UpdateInventories(x.getWorld(), x.getPos()));
    }
}
