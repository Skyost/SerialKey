package fr.skyost.serialkey.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.BlockIterator;

import fr.skyost.serialkey.SerialKeyAPI;

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
	 * 
	 * @return <b>true :</b> If the location has been corrected.
	 * <br /><b>false :</b> Otherwise.
	 */
	
	public static final boolean correctLocation(final Location location) {
		if(SerialKeyAPI.hasPadlock(location, false)) {
			return false;
		}
		final Block block = location.getBlock();
		final BlockState state = block.getState();
		if(state instanceof Chest) {
			final InventoryHolder holder = ((Chest)state).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				final Location left = ((Chest)((DoubleChest)holder).getLeftSide()).getLocation();
				if(SerialKeyAPI.hasPadlock(left, false)) {
					location.setX(left.getX());
					location.setZ(left.getZ());
					return true;
				}
				final Location right = ((Chest)((DoubleChest)holder).getRightSide()).getLocation();
				if(SerialKeyAPI.hasPadlock(right, false)) {
					location.setX(right.getX());
					location.setZ(right.getZ());
					return true;
				}
			}
			return false;
		}
		final MaterialData data = state.getData();
		if(DoorUtils.instanceOf(data)) {
			location.setY(DoorUtils.getBlockBelow(block).getY());
			if(SerialKeyAPI.hasPadlock(location, false)) {
				return true;
			}
			final Block doubleDoor = DoorUtils.getDoubleDoor(block);
			if(doubleDoor != null) {
				final Location doubleDoorLocation = doubleDoor.getLocation();
				doubleDoorLocation.setY(location.getY());
				if(SerialKeyAPI.hasPadlock(doubleDoorLocation, false)) {
					location.setX(doubleDoorLocation.getX());
					location.setZ(doubleDoorLocation.getZ());
				}
			}
			return true;
		}
		if(data instanceof TrapDoor) {
			final Block attached = block.getRelative(((TrapDoor)data).getAttachedFace());
			location.setX(attached.getX());
			location.setZ(attached.getZ());
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the block targeted by a LivingEntity.
	 * 
	 * @param entity The LivingEntity.
	 * @param range The range.
	 * 
	 * @return The block.
	 */
	
	public static final Block getTargetBlock(final LivingEntity entity, final int range) {
		final BlockIterator bit = new BlockIterator(entity, range);
		while(bit.hasNext()) {
			final Block next = bit.next();
			if(next != null && next.getType() != Material.AIR) {
				return next;
			}
		}
		return null;
	}

}
