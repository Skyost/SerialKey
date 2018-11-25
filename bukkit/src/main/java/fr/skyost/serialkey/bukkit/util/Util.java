package fr.skyost.serialkey.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

}