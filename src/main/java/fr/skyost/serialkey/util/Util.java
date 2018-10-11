package fr.skyost.serialkey.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Contains some useful methods.
 */

public class Util {
	
	/**
	 * Picks a random ChatColor.
	 * 
	 * @param exclude If you want to exclude some colors.
	 * 
	 * @return A random ChatColor.
	 */
	
	public static ChatColor randomChatColor(final ChatColor... exclude) {
		final List<ChatColor> excludeList = Arrays.asList(exclude);
		final ChatColor[] values = ChatColor.values();
		final Random random = new Random();

		ChatColor result;
		do {
			result = values[random.nextInt(values.length)];
		}
		while(excludeList.contains(result));
		return result;
	}
	
	/**
	 * Returns the map which contains only the specified objects.
	 * 
	 * @param map The map.
	 * @param objects The objects.
	 * 
	 * @return A map which contains only the objects (as keys).
	 */
	
	public static <V> Map<String, V> keepAll(final Map<String, V> map, final Collection<String> objects) {
		final Map<String, V> result = new HashMap<>();
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
	 * Creates a map.
	 * 
	 * @param keys The keys.
	 * @param values The values.
	 * 
	 * @return The map.
	 */
	
	public static <V, K> Map<K, V> createMap(final K[] keys, final V[] values) {
		if(keys.length != values.length) {
			return null;
		}
		final Map<K, V> map = new HashMap<>();
		for(int i = 0; i != keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}
	
	/**
	 * Creates an item with a custom name.
	 * 
	 * @param name The name.
	 * @param material The item's material.
	 * 
	 * @return The item.
	 */
	
	public static ItemStack createItem(final String name, final Material material) {
		final ItemStack item = new ItemStack(material);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Checks if the specified item is valid.
	 *
	 * @param item The item.
	 *
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */

	public static boolean isValidItem(final ItemStack item) {
		return (item != null && item.hasItemMeta()) && item.getItemMeta().hasDisplayName();
	}

}