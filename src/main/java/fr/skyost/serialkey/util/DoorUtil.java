package fr.skyost.serialkey.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

/**
 * Allows to perform some operations on doors.
 */

public class DoorUtil {

	/**
	 * Doors faces.
	 */

	private static final BlockFace[] FACES = new BlockFace[] {
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
	};
	
	/**
	 * Checks if the specified MaterialData is a Door.
	 * 
	 * @param data The MaterialData.
	 * 
	 * @return <b>true :<b> If the specified MaterialData.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static Door getInstance(final BlockData data) {
		return data instanceof Door ? (Door)data : null;
	}

	/**
	 * Returns the block below the specified block.
	 *
	 * @param block The block.
	 *
	 * @return The block below (if needed).
	 */

	public static Block getBlockBelow(final Block block) {
		final Door door = getInstance(block.getBlockData());
		if(door == null) {
			return null;
		}

		return door.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
	}
	
	/**
	 * Returns the other door of a double door.
	 * 
	 * @param door The door.
	 * 
	 * @return The other door.
	 */
	
	public static Block getDoubleDoor(final Block door) {
		for(final BlockFace face : FACES) {
			final Block relative = door.getRelative(face);
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

	public static boolean areConnected(final Block block1, final Block block2) {
		final Door door1;
		final Door door2;
		if((door1 = getInstance(block1.getBlockData())) == null || (door2 = getInstance(block2.getBlockData())) == null) {
			return false;
		}

		return door1.getFacing() == door2.getFacing() && door1.getHinge() != door2.getHinge();
	}
	
}
