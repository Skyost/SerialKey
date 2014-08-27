package fr.skyost.serialkey;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.skyost.serialkey.utils.Utils;

public class SerialKeyAPI {
	
	/**
	 * Gets the plugin's config.
	 * 
	 * @return The plugin's config.
	 */
	
	public static final PluginConfig getConfig() {
		return SerialKey.config;
	}
	
	/**
	 * Gets the plugin's message.
	 * 
	 * @return The plugin's message.
	 */
	
	public static final PluginMessages getMessages() {
		return SerialKey.messages;
	}
	
	/**
	 * Gets a clone of the key item.
	 * 
	 * @return A clone of the key item.
	 */
	
	public static final ItemStack getKeyItem() {
		return SerialKey.key.clone();
	}
	
	/**
	 * Gets a clone of the master key item.
	 * 
	 * @return A clone of the master key item.
	 */
	
	public static final ItemStack getMasterKeyItem() {
		return SerialKey.masterKey.clone();
	}
	
	public static final ItemStack getKeyCloneItem() {
		return SerialKey.keyClone.clone();
	}
	
	public static final void sendMessage(final CommandSender sender, final String message) {
		sender.sendMessage(SerialKey.messages.prefix + " " + message);
	}
	
	/**
	 * Creates a padlock for the selected location.
	 * 
	 * @param location The location.
	 */
	
	public static final void createPadlock(final Location location) {
		createPadlock(location, null);
	}
	
	/**
	 * Creates a padlock for the selected location.
	 * <br />It will format a key too.
	 * 
	 * @param location The location (can be corrected if needed).
	 * @param key The key.
	 */
	
	public static final void createPadlock(final Location location, final ItemStack key) {
		correctLocation(location);
		SerialKey.data.padlocks.add(location);
		if(key != null) {
			formatKey(location, key);
		}
	}
	
	/**
	 * Removes a padlock.
	 * 
	 * @param location
	 */
	
	public static final void removePadlock(final Location location) {
		removePadlock(location, null);
	}
	
	/**
	 * Removes a padlock.
	 * <br />It will format a key too.
	 * 
	 * @param location The location (can be corrected if needed).
	 * @param key The key.
	 */
	
	public static final void removePadlock(final Location location, final ItemStack key) {
		correctLocation(location);
		SerialKey.data.padlocks.remove(location);
		if(key != null) {
			final ItemMeta meta = key.getItemMeta();
			meta.setLore(null);
			key.setItemMeta(meta);
		}
	}
	
	/**
	 * Checks if the specified location has a padlock.
	 * 
	 * @param location The location (can be corrected if needed).
	 * 
	 * @return <b>true</b> : yes.
	 * <br /><b>false</b> : no.
	 */
	
	public static final boolean hasPadlock(final Location location) {
		return hasPadlock(location, true);
	}
	
	/**
	 * Checks if the specified location has a padlock.
	 * 
	 * @param location The location.
	 * @param correct If you want to automatically correct the location.
	 * 
	 * @return <b>true</b> : yes.
	 * <br /><b>false</b> : no.
	 */
	
	public static final boolean hasPadlock(final Location location, final boolean correct) {
		if(correct) {
			correctLocation(location);
		}
		return SerialKey.data.padlocks.contains(location);
	}
	
	/**
	 * Checks if the specified item is a valid key for the specified location.
	 * 
	 * @param key The key.
	 * @param location The location (will not be corrected).
	 * 
	 * @return <b>true</b> : yes.
	 * <br /><b>false</b> : no.
	 */
	
	public static final boolean isValidKey(final ItemStack key, final Location location) {
		return isValidKey(key, location, null);
	}
	
	/**
	 * Checks if the specified item is a valid key for the specified location.
	 * 
	 * @param key The key.
	 * @param location The location (can be corrected if needed).
	 * @param player If you want to check if the player has the right permission. Will send a message elsewhere.
	 * 
	 * @return <b>true</b> : yes.
	 * <br /><b>false</b> : no.
	 */
	
	public static final boolean isValidKey(final ItemStack key, final Location location, final Player player) {
		if(key == null) {
			return false;
		}
		final Material type = key.getType();
		if(type == Material.AIR || !key.hasItemMeta()) {
			return false;
		}
		final ItemMeta meta = key.getItemMeta();
		if(!meta.hasDisplayName() || !meta.hasLore()) {
			return false;
		}
		final List<String> lore = meta.getLore();
		final String name = meta.getDisplayName();
		if(type == SerialKey.masterKey.getType() && name.equals(SerialKey.masterKey.getItemMeta().getDisplayName())) {
			if(player != null && !player.hasPermission("serialkey.use.masterkey")) {
				player.sendMessage(SerialKey.messages.message6);
				return false;
			}
			return true;
		}
		if(type != SerialKey.key.getType() || !name.equals(SerialKey.key.getItemMeta().getDisplayName()) || lore == null || lore.size() != 2) {
			return false;
		}
		final World world = Bukkit.getWorld(ChatColor.stripColor(lore.get(0)));
		if(world == null) {
			return false;
		}
		final String[] rawLocation = ChatColor.stripColor(lore.get(1)).split(", ");
		if(rawLocation.length != 3) {
			return false;
		}
		correctLocation(location);
		if(!world.equals(location.getWorld()) || !rawLocation[0].equals(String.valueOf(location.getBlockX())) || !rawLocation[1].equals(String.valueOf(location.getBlockY())) || !rawLocation[2].equals(String.valueOf(location.getBlockZ()))) {
			return false;
		}
		if(player != null && !player.hasPermission("serialkey.use.key")) {
			player.sendMessage(SerialKey.messages.message6);
			return false;
		}
		return true;
	}
	
	/**
	 * Gets a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * 
	 * @return The key.
	 */
	
	public static final ItemStack getKey(final Location location) {
		return formatKey(location, getKeyItem());
	}
	
	/**
	 * Formats a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * @param key The key.
	 * 
	 * @return The formatted key.
	 */
	
	public static final ItemStack formatKey(final Location location, final ItemStack key) {
		if(key.getAmount() > 1) {
			key.setAmount(1);
		}
		final ChatColor color = Utils.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC);
		final ItemMeta meta = key.getItemMeta();
		meta.setLore(Arrays.asList(color + location.getWorld().getName(), color + String.valueOf(location.getBlockX()) + ", " + location.getBlockY() + ", " + location.getBlockZ()));
		key.setItemMeta(meta);
		return key;
	}
	
	/**
	 * Correct a location (used to handle doors because they are composed from two blocks and double chests too).
	 * 
	 * @param location The location.
	 */
	
	private static final void correctLocation(final Location location) {
		final Block block = location.getBlock();
		final BlockState state = block.getState();
		if(state instanceof Chest) {
			final InventoryHolder holder = ((Chest)state).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				final Location left = ((Chest)((DoubleChest)holder).getLeftSide()).getLocation();
				location.setX(left.getX());
				location.setZ(left.getZ());
			}
		}
		else if(state.getData() instanceof org.bukkit.material.Door) {
			location.setY(location.getBlockY() - (block.getRelative(BlockFace.DOWN).getState().getData() instanceof org.bukkit.material.Door ?  2 : 1));
		}
	}

}
