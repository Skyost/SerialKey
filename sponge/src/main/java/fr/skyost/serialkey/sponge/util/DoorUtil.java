package fr.skyost.serialkey.sponge.util;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Allows to perform some operations on doors.
 */

public class DoorUtil {

	/**
	 * Doors faces.
	 */

	private static final Direction[] DIRECTIONS = new Direction[]{
			Direction.NORTH,
			Direction.EAST,
			Direction.SOUTH,
			Direction.WEST
	};

	/**
	 * Checks if the block located at specified location is a Door.
	 *
	 * @param location The location.
	 *
	 * @return <b>true :<b> If there is a door.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static boolean isDoor(final Location<World> location) {
		return isDoor(location.getBlockType());
	}

	/**
	 * Checks if the specified block type is a door.
	 *
	 * @param type The block type.
	 *
	 * @return <b>true :<b> If it's a door.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static boolean isDoor(final BlockType type) {
		return type.equals(BlockTypes.ACACIA_DOOR)
				|| type.equals(BlockTypes.BIRCH_DOOR)
				|| type.equals(BlockTypes.DARK_OAK_DOOR)
				|| type.equals(BlockTypes.IRON_DOOR)
				|| type.equals(BlockTypes.JUNGLE_DOOR)
				|| type.equals(BlockTypes.SPRUCE_DOOR)
				|| type.equals(BlockTypes.WOODEN_DOOR);
	}

	/**
	 * Checks if the specified block type is a trapdoor.
	 *
	 * @param type The block type.
	 *
	 * @return <b>true :<b> If it's a trapdoor.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static boolean isTrapdoor(final BlockType type) {
		return type.equals(BlockTypes.TRAPDOOR) || type.equals(BlockTypes.IRON_TRAPDOOR);
	}

	/**
	 * Returns the block below the specified block.
	 *
	 * @param block The block.
	 *
	 * @return The block below (if needed).
	 */

	public static Location<World> getBlockBelow(final Location<World> block) {
		if(!isDoor(block)) {
			return null;
		}

		// TODO: Check with two doors : one below the other.
		final Location<World> below = block.getRelative(Direction.DOWN);
		return isDoor(below) ? below : block;
	}
	
	/**
	 * Returns the other door of a double door.
	 * 
	 * @param door The door.
	 * 
	 * @return The other door.
	 */
	
	public static Location<World> getDoubleDoor(final Location<World> door) {
		for(final Direction direction : DIRECTIONS) {

			final Location<World> relative = door.getRelative(direction);
			if(areConnected(door, relative)) {
				return getBlockBelow(relative);
			}
		}
		return null;
	}
	
	/**
	 * Check if two doors are connected.
	 * 
	 * @param block1 The block one.
	 * @param block2 The block two.
	 * 
	 * @return <b>true :</b> If the doors are connected.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static boolean areConnected(final Location<World> block1, final Location<World> block2) {
		if(!isDoor(block1) || !isDoor(block2)) {
			return false;
		}

		return block1.getBlock().get(Keys.DIRECTION).equals(block2.getBlock().get(Keys.DIRECTION)) && block1.getBlock().get(Keys.HINGE_POSITION).equals(block2.getBlock().get(Keys.HINGE_POSITION));
	}
	
}