package fr.skyost.serialkey.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {
	
	/**
	 * Picks a random ChatColor.
	 * 
	 * @param exclude If you want to exclude some colors.
	 * 
	 * @return A random ChatColor.
	 */
	
	public static final ChatColor randomChatColor(final ChatColor... exclude) {
		final List<ChatColor> excludeList = Arrays.asList(exclude);
		final ChatColor[] values = ChatColor.values();
		final Random random = new Random();
		while(true) {
			final ChatColor randomColor = values[random.nextInt(values.length)];
			if(!excludeList.contains(randomColor)) {
				return randomColor;
			}
		}
	}
	
	/**
	 * Inverse of addAll(...).
	 * 
	 * @param map The map.
	 * @param objects The objects.
	 * 
	 * @return A map which contains only the objects (as keys).
	 */
	
	public static final <V> Map<String, V> keepAll(final Map<String, V> map, final Collection<String> objects) {
		final Map<String, V> result = new HashMap<String, V>();
		for(final String object : objects) {
			final char[] chars = new char[object.length()];
			object.getChars(0, object.length() > 3 ? 3 : object.length(), chars, 0);
			for(final char c : chars) {
				if(c == ' ') {
					continue;
				}
				final String character = String.valueOf(c);
				if(map.containsKey(character)) {
					result.put(character, map.get(character));
				}
			}
		}
		return result;
	}
	
	/**
	 * Checks if the specified item is valid.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isValidItem(final ItemStack item) {
		if(item != null && item.hasItemMeta()) {
			final ItemMeta meta = item.getItemMeta();
			if(!meta.hasDisplayName()) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Corrects a location (used to handle doors because they are composed from two blocks and double chests too).
	 * 
	 * @param location The location.
	 */
	
	public static final boolean correctLocation(final Location location) {
		final Block block = location.getBlock();
		final BlockState state = block.getState();
		if(state instanceof Chest) {
			final InventoryHolder holder = ((Chest)state).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				final Location left = ((Chest)((DoubleChest)holder).getLeftSide()).getLocation();
				location.setX(left.getX());
				location.setZ(left.getZ());
				return true;
			}
		}
		else if(state.getData() instanceof org.bukkit.material.Door) { // TODO Handle double doors.
			location.setY(location.getBlockY() - (block.getRelative(BlockFace.DOWN).getState().getData() instanceof org.bukkit.material.Door ?  2 : 1));
			return true;
		}
		return false;
	}

}
