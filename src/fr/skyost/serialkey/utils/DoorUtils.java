package fr.skyost.serialkey.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;

public class DoorUtils {
	
	/**
	 * Checks if the specified MaterialData is a Door.
	 * 
	 * @param data The MaterialData.
	 * 
	 * @return <b>true :<b> If the specified MaterialData.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static final boolean instanceOf(final MaterialData data) {
		return data instanceof Door;
	}
	
	/**
	 * Gets the block below a door.
	 * 
	 * @param door The door.
	 * 
	 * @return The block.
	 */

	public static final Block getBlockBelow(final Block door) {
		final Location location = door.getLocation();
		location.setY(location.getBlockY() - (instanceOf(door.getRelative(BlockFace.DOWN).getState().getData()) ? 2 : 1));
		return location.getBlock();
	}
	
	/**
	 * Gets the other door of a double door.
	 * 
	 * @param door The door.
	 * 
	 * @return The other door.
	 */
	
	public static final Block getDoubleDoor(final Block door) {
		final Block bottomPart = getBlockBelow(door).getRelative(BlockFace.UP);
		for(final BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
			final Block relative = bottomPart.getRelative(face);
			if(instanceOf(relative.getState().getData()) && areConnected(bottomPart, relative)) {
				return relative;
			}
		}
		return null;
	}
	
	/**
	 * Check if two doors are connected.
	 * 
	 * @param door1 The door one.
	 * @param door2 The door two.
	 * 
	 * @return <b>true :</b> If the doors are connected.
	 * <br /><b>false :</b> Otherwise.
	 */

	public static final boolean areConnected(final Block door1, final Block door2) {
		if(door1.getType() != door2.getType()) {
			return false;
		}
		if((door1.getRelative(BlockFace.UP).getData() & 0x1) == (door2.getRelative(BlockFace.UP).getData() & 0x1)) {
			return false;
		}
		return (door1.getData() & 0x3) == (door2.getData() & 0x3);
	}
	
}
