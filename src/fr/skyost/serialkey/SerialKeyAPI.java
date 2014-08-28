package fr.skyost.serialkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
	
	/**
	 * Gets the key clone item.
	 * 
	 * @return The key clone item.
	 */
	
	public static final ItemStack getKeyCloneItem() {
		return SerialKey.keyClone.clone();
	}
	
	/**
	 * Sends a message with the plugin's prefix.
	 * 
	 * @param sender Who receive the message.
	 * @param message The message.
	 */
	
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
		Utils.correctLocation(location);
		SerialKey.data.padlocks.add(location);
		if(isBlankKey(key)) {
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
		Utils.correctLocation(location);
		SerialKey.data.padlocks.remove(location);
		if(isUsedKey(key)) {
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
	 * @param correctLocation If you want to automatically correct the location.
	 * 
	 * @return <b>true</b> : yes.
	 * <br /><b>false</b> : no.
	 */
	
	public static final boolean hasPadlock(final Location location, final boolean correctLocation) {
		if(correctLocation) {
			Utils.correctLocation(location);
		}
		return SerialKey.data.padlocks.contains(location);
	}
	
	/**
	 * Checks if the specified item is a key (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isKey(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == SerialKey.config.keyMaterial && item.getItemMeta().getDisplayName().equals(SerialKey.config.keyName);
	}
	
	/**
	 * Checks if the specified item is a blank key.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isBlankKey(final ItemStack item) {
		return isKey(item) && !item.getItemMeta().hasLore();
	}
	
	/**
	 * Checks if the specified item is an used key.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isUsedKey(final ItemStack item) {
		if(!isKey(item)) {
			return false;
		}
		final List<String> lore = item.getItemMeta().getLore();
		return lore != null && lore.size() == 2;
	}
	
	/**
	 * Checks if the specified item is a master key.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isMasterKey(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == SerialKey.config.masterKeyMaterial && item.getItemMeta().getDisplayName().equals(SerialKey.config.masterKeyName);
	}
	
	/**
	 * Checks if the specified item is a bunch of keys (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isBunchOfKeys(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == SerialKey.config.bunchOfKeysMaterial && item.getItemMeta().getDisplayName().equals(SerialKey.config.bunchOfKeysName);
	}
	
	/**
	 * Checks if the specified inventory is a bunch of keys (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isBunchOfKeys(final Inventory inventory) {
		return inventory.getName().equals(SerialKey.config.bunchOfKeysName) && inventory.getSize() == 9;
	}
	
	/**
	 * Checks if the specified item is a blank bunch of keys.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isBlankBunchOfKeys(final ItemStack item) {
		return isBunchOfKeys(item) && !item.getItemMeta().hasLore();
	}
	
	/**
	 * Checks if the specified item is an used bunch of keys.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br /><b>false :</b> no.
	 */
	
	public static final boolean isUsedBunchOfKeys(final ItemStack item) {
		return isBunchOfKeys(item) && item.getItemMeta().hasLore();
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
		if(isMasterKey(key)) {
			if(player != null && !player.hasPermission("serialkey.use.masterkey")) {
				sendMessage(player, SerialKey.messages.message6);
			}
			return true;
		}
		Utils.correctLocation(location);
		final Location keyLocation = extractLocation(key);
		if(keyLocation != null && keyLocation.equals(location)) {
			if(player != null && !player.hasPermission("serialkey.use.key")) {
				sendMessage(player, SerialKey.messages.message6);
			}
			return true;
		}
		final ItemStack[] extractedKeys = extractKeys(key);
		if(extractedKeys != null) {
			if(player != null && !player.hasPermission("serialkey.use.bunchofkeys")) {
				sendMessage(player, SerialKey.messages.message6);
				return true;
			}
			for(final ItemStack extractedKey : extractedKeys) {
				if(isValidKey(extractedKey, location, null)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Extracts a location from a key.
	 * 
	 * @param key The key.
	 * 
	 * @return The location.
	 */
	
	public static final Location extractLocation(final ItemStack key) {
		if(!isUsedKey(key)) {
			return null;
		}
		final List<String> lore = key.getItemMeta().getLore();
		final World world = Bukkit.getWorld(ChatColor.stripColor(lore.get(0)));
		if(world == null) {
			return null;
		}
		final String[] rawLocation = ChatColor.stripColor(lore.get(1)).split(", ");
		if(rawLocation.length != 3) {
			return null;
		}
		final Location itemLocation = new Location(world, Integer.parseInt(rawLocation[0]), Integer.parseInt(rawLocation[1]), Integer.parseInt(rawLocation[2]));
		if(Utils.correctLocation(itemLocation)) {
			formatKey(itemLocation, key);
		}
		return itemLocation;
	}
	
	/**
	 * Gets a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * 
	 * @return The key.
	 */
	
	public static final ItemStack getKey(final Location location) {
		final ItemStack key = getKeyItem();
		formatKey(location, key);
		return key;
	}
	
	/**
	 * Formats a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * @param key The key.
	 */
	
	public static final void formatKey(final Location location, final ItemStack key) {
		if(!isKey(key)) {
			return;
		}
		final ChatColor color = Utils.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC);
		final ItemMeta meta = key.getItemMeta();
		meta.setLore(Arrays.asList(color + location.getWorld().getName(), color + String.valueOf(location.getBlockX()) + ", " + location.getBlockY() + ", " + location.getBlockZ()));
		key.setItemMeta(meta);
	}
	
	/**
	 * Adds a key to a bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys.
	 * @param key The key.
	 */
	
	public static final void addKey(final ItemStack bunchOfKeys, final ItemStack key) {
		if(!isBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
			return;
		}
		final ItemMeta meta = bunchOfKeys.getItemMeta();
		final List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
		lore.addAll(key.getItemMeta().getLore());
		meta.setLore(lore);
		bunchOfKeys.setItemMeta(meta);
	}
	
	/**
	 * Removes a key from a bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys.
	 * @param key The key.
	 */
	
	public static final void removeKey(final ItemStack bunchOfKeys, final ItemStack key) {
		if(!isUsedBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
			return;
		}
		final ItemMeta meta = bunchOfKeys.getItemMeta();
		final List<String> lore = new ArrayList<String>(meta.getLore());
		final List<String> keyLore = key.getItemMeta().getLore();
		for(int i = 0; i != lore.size(); i++) {
			final List<String> data = Arrays.asList(lore.get(i), lore.get(++i));
			if(data.equals(keyLore)) {
				lore.removeAll(data);
				break;
			}
		}
		meta.setLore(lore.size() == 0 ? null : lore);
		key.setItemMeta(meta);
	}
	
	/**
	 * Clear a bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys.
	 */
	
	public static final void clearKeys(final ItemStack bunchOfKeys) {
		if(!isUsedBunchOfKeys(bunchOfKeys)) {
			return;
		}
		final ItemMeta meta = bunchOfKeys.getItemMeta();
		meta.setLore(null);
		bunchOfKeys.setItemMeta(meta);
	}
	
	/**
	 * Extracts a key from a bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys.
	 * 
	 * @return The keys.
	 */
	
	public static final ItemStack[] extractKeys(final ItemStack bunchOfKeys) {
		if(!isUsedBunchOfKeys(bunchOfKeys)) {
			return null;
		}
		final List<String> lore = bunchOfKeys.getItemMeta().getLore();
		final List<ItemStack> keys = new ArrayList<ItemStack>();
		for(int i = 0; i != lore.size(); i++) {
			final ItemStack blankKey = getKeyItem();
			final ItemMeta blankMeta = blankKey.getItemMeta();
			blankMeta.setLore(Arrays.asList(lore.get(i), lore.get(++i)));
			blankKey.setItemMeta(blankMeta);
			keys.add(blankKey);
		}
		return keys.toArray(new ItemStack[keys.size()]);
	}
	
	
	
	public static final Inventory createInventory(final ItemStack bunchOfKeys) {
		return createInventory(bunchOfKeys, new Player[]{});
	}
	
	public static final Inventory createInventory(final ItemStack bunchOfKeys, final Player... players) {
		if(!isBunchOfKeys(bunchOfKeys)) {
			return null;
		}
		final Inventory inventory = Bukkit.createInventory(null, 9, SerialKeyAPI.getConfig().bunchOfKeysName);
		final ItemStack[] keys = extractKeys(bunchOfKeys);
		if(keys != null && keys.length != 0) {
			inventory.addItem(keys);
		}
		if(players != null && players.length != 0) {
			for(final Player player : players) {
				player.openInventory(inventory);
			}
		}
		return inventory;
	}
	
}
