package fr.skyost.serialkey.sponge.util

import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * Allows to perform some operations on doors.
 */
object DoorUtil {
    /**
     * Doors faces.
     */
    private val DIRECTIONS = arrayOf(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    )

    /**
     * Checks if the block located at specified location is a Door.
     *
     * @param location The location.
     *
     * @return **true :** If there is a door.
     * <br></br>**false :** Otherwise.
     */
    fun isDoor(location: Location<World>): Boolean {
        return isDoor(location.blockType)
    }

    /**
     * Checks if the specified block type is a door.
     *
     * @param type The block type.
     *
     * @return **true :** If it's a door.
     * <br></br>**false :** Otherwise.
     */
    @JvmStatic
    fun isDoor(type: BlockType): Boolean {
        return type == BlockTypes.ACACIA_DOOR || type == BlockTypes.BIRCH_DOOR || type == BlockTypes.DARK_OAK_DOOR || type == BlockTypes.IRON_DOOR || type == BlockTypes.JUNGLE_DOOR || type == BlockTypes.SPRUCE_DOOR || type == BlockTypes.WOODEN_DOOR
    }

    /**
     * Checks if the specified block type is a trapdoor.
     *
     * @param type The block type.
     *
     * @return **true :** If it's a trapdoor.
     * <br></br>**false :** Otherwise.
     */
    @JvmStatic
    fun isTrapdoor(type: BlockType): Boolean {
        return type == BlockTypes.TRAPDOOR || type == BlockTypes.IRON_TRAPDOOR
    }

    /**
     * Returns the block below the specified block.
     *
     * @param block The block.
     *
     * @return The block below (if needed).
     */
    @JvmStatic
    fun getBlockBelow(block: Location<World>): Location<World>? {
        if (!isDoor(block)) {
            return null
        }

        // TODO: Check with two doors : one below the other.
        val below = block.getRelative(Direction.DOWN)
        return if (isDoor(below)) below else block
    }

    /**
     * Returns the other door of a double door.
     *
     * @param door The door.
     *
     * @return The other door.
     */
    @JvmStatic
    fun getDoubleDoor(door: Location<World>): Location<World>? {
        for (direction in DIRECTIONS) {
            val relative = door.getRelative(direction)
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
    private fun areConnected(block1: Location<World>, block2: Location<World>): Boolean {
        return if (!isDoor(block1) || !isDoor(block2)) {
            false
        } else block1.block.get(Keys.DIRECTION) == block2.block.get(Keys.DIRECTION) && block1.block.get(Keys.HINGE_POSITION) == block2.block.get(Keys.HINGE_POSITION)
    }
}