package fr.skyost.serialkey;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.skyost.serialkey.utils.CryptoUtils;
import fr.skyost.serialkey.utils.Utils;

public class SerialKeyAPI {
	
	private static final SerialKey PLUGIN = (SerialKey)Bukkit.getPluginManager().getPlugin("SerialKey");
	
	/**
	 * The encryption key (will be used if specified in the configuration).
	 */
	
	public static final String SERIAL_KEY_ENCRYPTION_KEY = "SerialKey";
	
	/**
	 * Gets the SerialKey's instance.
	 * 
	 * @return The SerialKey's instance.
	 */
	
	public static final SerialKey getPlugin() {
		return PLUGIN;
	}
	
	/**
	 * Gets the plugin's config.
	 * 
	 * @return The plugin's config.
	 */
	
	public static final PluginConfig getConfig() {
		return PLUGIN.config;
	}
	
	/**
	 * Gets the plugin's message.
	 * 
	 * @return The plugin's message.
	 */
	
	public static final PluginMessages getMessages() {
		return PLUGIN.messages;
	}
	
	/**
	 * Gets a clone of the key item.
	 * 
	 * @return A clone of the key item.
	 */
	
	public static final ItemStack getKeyItem() {
		return PLUGIN.key.clone();
	}
	
	/**
	 * Gets a clone of the master key item.
	 * 
	 * @return A clone of the master key item.
	 */
	
	public static final ItemStack getMasterKeyItem() {
		return PLUGIN.masterKey.clone();
	}
	
	/**
	 * Gets the key clone item.
	 * 
	 * @return The key clone item.
	 */
	
	public static final ItemStack getKeyCloneItem() {
		return PLUGIN.keyClone.clone();
	}
	
	/**
	 * Gets the padlock finder item.
	 * 
	 * @return The padlock finder item.
	 */
	
	public static final ItemStack getPadlockFinderItem() {
		return PLUGIN.padlockFinder.clone();
	}
	
	/**
	 * Sends a message with the plugin's prefix.
	 * 
	 * @param sender Who receives the message.
	 * @param message The message.
	 */
	
	public static final void sendMessage(final CommandSender sender, final String message) {
		sender.sendMessage(PLUGIN.messages.prefix + " " + message);
	}
	
	/**
	 * Creates a padlock for the selected location.
	 * 
	 * @param location The location.
	 * 
	 * @throws UnsupportedEncodingException If an exception occurs while encrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final void createPadlock(final Location location) throws UnsupportedEncodingException, GeneralSecurityException {
		createPadlock(location, null);
	}
	
	/**
	 * Creates a padlock for the selected location.
	 * <br>It will format a key too.
	 * 
	 * @param location The location (can be corrected if needed).
	 * @param key The key.
	 * 
	 * @throws UnsupportedEncodingException If an exception occurs while encrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final void createPadlock(final Location location, final ItemStack key) throws UnsupportedEncodingException, GeneralSecurityException {
		Utils.correctLocation(location);
		PLUGIN.data.padlocks.add(location);
		if(isBlankKey(key)) {
			formatItem(location, key);
		}
	}
	
	/**
	 * Removes a padlock.
	 * 
	 * @param location The location (can be corrected if needed).
	 */
	
	public static final void removePadlock(final Location location) {
		removePadlock(location, null);
	}
	
	/**
	 * Removes a padlock.
	 * <br>It will format a key too.
	 * 
	 * @param location The location (can be corrected if needed).
	 * @param key The key.
	 */
	
	public static final void removePadlock(final Location location, final ItemStack key) {
		Utils.correctLocation(location);
		PLUGIN.data.padlocks.remove(location);
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
	 * <br><b>false</b> : no.
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
	 * <br><b>false</b> : no.
	 */
	
	public static final boolean hasPadlock(final Location location, final boolean correctLocation) {
		if(correctLocation) {
			Utils.correctLocation(location);
		}
		return PLUGIN.data.padlocks.contains(location);
	}
	
	/**
	 * Checks if the specified item is a key (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isKey(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == PLUGIN.config.keyMaterial && (PLUGIN.config.canRenameItems ? true : item.getItemMeta().getDisplayName().equals(PLUGIN.config.keyName));
	}
	
	/**
	 * Checks if the specified item is a blank key.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
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
	 * <br><b>false :</b> no.
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
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isMasterKey(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == PLUGIN.config.masterKeyMaterial && (PLUGIN.config.canRenameItems ? true : item.getItemMeta().getDisplayName().equals(PLUGIN.config.masterKeyName));
	}
	
	/**
	 * Checks if the specified item is a bunch of keys (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isBunchOfKeys(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == PLUGIN.config.bunchOfKeysMaterial && (PLUGIN.config.canRenameItems ? true : item.getItemMeta().getDisplayName().equals(PLUGIN.config.bunchOfKeysName));
	}
	
	/**
	 * Checks if the specified inventory is a bunch of keys (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isBunchOfKeys(final Inventory inventory) {
		return inventory.getName().equals(PLUGIN.config.bunchOfKeysName) && inventory.getSize() == 9;
	}
	
	/**
	 * Checks if the specified item is a blank bunch of keys.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
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
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isUsedBunchOfKeys(final ItemStack item) {
		return isBunchOfKeys(item) && item.getItemMeta().hasLore();
	}
	
	/**
	 * Checks if the specified item is a padlock finder (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isPadlockFinder(final ItemStack item) {
		return Utils.isValidItem(item) && item.getType() == Material.COMPASS && item.getItemMeta().getDisplayName().equals(PLUGIN.config.padlockFinderName);
	}
	
	/**
	 * Checks if the specified item is a blank padlock finder.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isBlankPadlockFinder(final ItemStack item) {
		return isPadlockFinder(item) && !item.getItemMeta().hasLore();
	}
	
	/**
	 * Checks if the specified item is an used padlock finder.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public static final boolean isUsedPadlockFinder(final ItemStack item) {
		return isPadlockFinder(item) && item.getItemMeta().hasLore();
	}
	
	/**
	 * Checks if the specified item is a valid key for the specified location.
	 * 
	 * @param key The key.
	 * @param location The location (will not be corrected).
	 * 
	 * @return <b>true</b> : yes.
	 * <br><b>false</b> : no.
	 * 
	 * @throws IOException If an exception occurs while decrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final boolean isValidKey(final ItemStack key, final Location location) throws GeneralSecurityException, IOException {
		return isValidKey(key, location, null);
	}
	
	/**
	 * Checks if the specified item is a valid key for the specified location.
	 * 
	 * @param key The key.
	 * @param location The location (can be corrected if needed).
	 * @param player If you want to check if the player has the right permission. Will send a message otherwise.
	 * 
	 * @return <b>true</b> : yes.
	 * <br><b>false</b> : no.
	 * 
	 * @throws IOException If an exception occurs while decrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final boolean isValidKey(final ItemStack key, final Location location, final Player player) throws GeneralSecurityException, IOException {
		if(isMasterKey(key)) {
			if(player != null && !player.hasPermission("serialkey.use.masterkey")) {
				sendMessage(player, PLUGIN.messages.messagePermission);
			}
			return true;
		}
		Utils.correctLocation(location);
		final Location keyLocation = extractLocation(key);
		if(keyLocation != null && keyLocation.equals(location)) {
			if(player != null && !player.hasPermission("serialkey.use.key")) {
				sendMessage(player, PLUGIN.messages.messagePermission);
			}
			return true;
		}
		final ItemStack[] extractedKeys = extractKeys(key);
		if(extractedKeys != null) {
			if(player != null && !player.hasPermission("serialkey.use.bunchofkeys")) {
				sendMessage(player, PLUGIN.messages.messagePermission);
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
	 * Extracts a location from a key or a padlock finder.
	 * 
	 * @param item The item.
	 * 
	 * @return The location.
	 * 
	 * @throws IOException If an exception occurs while decrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final Location extractLocation(final ItemStack item) throws GeneralSecurityException, IOException {
		boolean isKey = isUsedKey(item);
		if(!isKey && !isUsedPadlockFinder(item)) {
			return null;
		}
		final List<String> lore = item.getItemMeta().getLore();
		final World world = Bukkit.getWorld(PLUGIN.config.encryptLore ? CryptoUtils.decrypt(ChatColor.stripColor(lore.get(0)), SERIAL_KEY_ENCRYPTION_KEY) : ChatColor.stripColor(lore.get(0)));
		if(world == null) {
			return null;
		}
		final String[] rawLocation = (PLUGIN.config.encryptLore ? CryptoUtils.decrypt(ChatColor.stripColor(lore.get(1)), SERIAL_KEY_ENCRYPTION_KEY) : ChatColor.stripColor(lore.get(1))).split(", ");
		if(rawLocation.length != 3) {
			return null;
		}
		final Location itemLocation = new Location(world, Integer.parseInt(rawLocation[0]), Integer.parseInt(rawLocation[1]), Integer.parseInt(rawLocation[2]));
		if(isKey && Utils.correctLocation(itemLocation)) {
			formatItem(itemLocation, item);
		}
		return itemLocation;
	}
	
	/**
	 * Gets a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * 
	 * @return The key.
	 * 
	 * @throws UnsupportedEncodingException If an exception occurs while encrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final ItemStack getKey(final Location location) throws UnsupportedEncodingException, GeneralSecurityException {
		final ItemStack key = getKeyItem();
		formatItem(location, key);
		return key;
	}
	
	/**
	 * Gets a padlock finder for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * 
	 * @return The padlock finder.
	 * 
	 * @throws UnsupportedEncodingException If an exception occurs while encrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final ItemStack getPadlockFinder(final Location location) throws UnsupportedEncodingException, GeneralSecurityException {
		final ItemStack padlockFinder = getPadlockFinderItem();
		formatItem(location, padlockFinder);
		return padlockFinder;
	}
	
	/**
	 * Formats a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * @param item The key.
	 * 
	 * @throws UnsupportedEncodingException If an exception occurs while encrypting the lore.
	 * @throws GeneralSecurityException Same here.
	 */
	
	public static final void formatItem(final Location location, final ItemStack item) throws UnsupportedEncodingException, GeneralSecurityException {
		if(!isKey(item) && !isPadlockFinder(item)) {
			return;
		}
		final ChatColor color = Utils.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC);
		final ItemMeta meta = item.getItemMeta();
		if(PLUGIN.config.encryptLore) {
			meta.setLore(Arrays.asList(color + CryptoUtils.encrypt(SERIAL_KEY_ENCRYPTION_KEY, location.getWorld().getName()), CryptoUtils.encrypt(SERIAL_KEY_ENCRYPTION_KEY, color + String.valueOf(location.getBlockX()) + ", " + location.getBlockY() + ", " + location.getBlockZ())));
		}
		else {
			meta.setLore(Arrays.asList(color + location.getWorld().getName(), color + String.valueOf(location.getBlockX()) + ", " + location.getBlockY() + ", " + location.getBlockZ()));
		}
		item.setItemMeta(meta);
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
	 * 
	 * @return The number of deleted keys.
	 */
	
	public static final short removeKey(final ItemStack bunchOfKeys, final ItemStack key) {
		if(!isUsedBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
			return 0;
		}
		final ItemMeta meta = bunchOfKeys.getItemMeta();
		final List<String> lore = meta.getLore();
		final List<String> keyLore = key.getItemMeta().getLore();
		for(final String line : new ArrayList<String>(keyLore)) {
			keyLore.remove(line);
			keyLore.add(ChatColor.stripColor(line));
		}
		short deleted = 0;
		for(int i = 0; i != lore.size(); i++) {
			final String world = lore.get(i);
			final String location = lore.get(++i);
			if(keyLore.equals(Arrays.asList(ChatColor.stripColor(world), ChatColor.stripColor(location))) && lore.removeAll(Arrays.asList(world, location))) {
				deleted++;
			}
		}
		meta.setLore(lore.size() == 0 ? null : lore);
		bunchOfKeys.setItemMeta(meta);
		return deleted;
	}
	
	/**
	 * Clears a bunch of keys.
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
	
	/**
	 * Creates an inventory for the specified bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys item.
	 * 
	 * @return The inventory.
	 */
	
	public static final Inventory createInventory(final ItemStack bunchOfKeys) {
		return createInventory(bunchOfKeys, new Player[]{});
	}
	
	/**
	 * Creates an inventory for the specified bunch of keys and open it for the specified players.
	 * 
	 * @param bunchOfKeys The bunch of keys item.
	 * @param players The players.
	 * 
	 * @return The inventory.
	 */
	
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