package fr.skyost.serialkey.bukkit.util

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Door

/**
 * Allows to perform some operations on doors.
 */
object DoorUtil {
    /**
     * Doors faces.
     */
    private val FACES = arrayOf(
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    )

    /**
     * Checks if the specified BlockData is a Door.
     *
     * @param data The BlockData.
     *
     * @return **true :** If the specified BlockData is a door.
     * <br></br>**false :** Otherwise.
     */
    fun getInstance(data: BlockData?): Door? {
        return if (data is Door) data else null
    }

    /**
     * Returns the block below the specified block.
     *
     * @param block The block.
     *
     * @return The block below (if needed).
     */
    fun getBlockBelow(block: Block): Block? {
        val door = getInstance(block.blockData) ?: return null
        return if (door.half == Bisected.Half.TOP) block.getRelative(BlockFace.DOWN) else block
    }

    /**
     * Returns the other door of a double door.
     *
     * @param door The door.
     *
     * @return The other door.
     */
    fun getDoubleDoor(door: Block): Block? {
        for (face in FACES) {
            val relative = door.getRelative(face)
            if (areConnected(door, relative)) {
                return getBlockBelow(relative)
            }
        }
        return null
    }

    /**
     * Check if two doors are connected.
     *
     * @param block1 The block one.
     * @param block2 The block two.
     *
     * @return **true :** If the doors are connected.
     * <br></br>**false :** Otherwise.
     */
    private fun areConnected(block1: Block, block2: Block): Boolean {
        var door1: Door?
        var door2: Door? = null
        return if (getInstance(block1.blockData).also { door1 = it!! } == null || getInstance(block2.blockData).also { door2 = it!! } == null) {
            false
        } else door1!!.facing == door2!!.facing && door1!!.hinge != door2!!.hinge
    }
}