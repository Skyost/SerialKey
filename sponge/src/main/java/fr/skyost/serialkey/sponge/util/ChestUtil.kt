package fr.skyost.serialkey.sponge.util

import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.block.tileentity.TileEntity
import org.spongepowered.api.block.tileentity.carrier.Chest
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * Allows to perform some operations on chests.
 */
object ChestUtil {
    /**
     * Returns the chest instance at the specified location.
     *
     * @param location The location.
     *
     * @return The chest instance.
     */
    @JvmStatic
    fun getChest(location: Location<World>): Chest? {
        val chest = location.tileEntity
        return chest.filter { entity: TileEntity? -> entity is Chest }.orElse(null) as Chest
    }

    /**
     * Returns whether the specified block type is a chest.
     *
     * @param type The block type.
     *
     * @return Whether the specified block type is a chest.
     */
    @JvmStatic
    fun isChest(type: BlockType): Boolean {
        return type == BlockTypes.CHEST || type == BlockTypes.TRAPPED_CHEST
    }
}