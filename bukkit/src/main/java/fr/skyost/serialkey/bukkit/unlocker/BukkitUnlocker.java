package fr.skyost.serialkey.bukkit.unlocker;

import fr.skyost.serialkey.bukkit.SerialKey;
import fr.skyost.serialkey.bukkit.util.Util;
import fr.skyost.serialkey.core.unlocker.PluginUnlocker;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * The Bukkit unlocker class.
 */

public class BukkitUnlocker extends PluginUnlocker<ItemStack> {

	/**
	 * Creates a new Bukkit unlocker instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public BukkitUnlocker(final SerialKey plugin) {
		super(plugin);
	}

	@Override
	protected String randomColor() {
		return Util.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC, ChatColor.BLACK).toString();
	}

	@Override
	protected String stripColor(final String string) {
		return ChatColor.stripColor(string);
	}

}