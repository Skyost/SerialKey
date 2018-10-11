package fr.skyost.serialkey;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.skyost.serialkey.config.PluginConfig;
import fr.skyost.serialkey.util.DoorUtil;
import fr.skyost.serialkey.util.ROT47;
import fr.skyost.serialkey.util.Util;

/**
 * The SerialKey API.
 */

public class SerialKeyAPI {

	/**
	 * The plugin instance.
	 */

	private final SerialKey plugin;

	/**
	 * The key item.
	 */

	private ItemStack key;

	/**
	 * The master key item.
	 */

	private ItemStack masterKey;

	/**
	 * The key clone item.
	 */

	private ItemStack keyClone;

	/**
	 * The bunch of keys item.
	 */

	private ItemStack bunchOfKeys;

	/**
	 * The padlock finder item.
	 */

	private ItemStack padlockFinder;
	
	SerialKeyAPI(final SerialKey plugin) {
		this.plugin = plugin;

		final PluginConfig config = plugin.getPluginConfig();
		key = Util.createItem(config.keyName, config.keyMaterial);
		masterKey = Util.createItem(config.masterKeyName, config.masterKeyMaterial);
		keyClone = key.clone();
		keyClone.setAmount(2);
		bunchOfKeys = Util.createItem(config.bunchOfKeysName, config.bunchOfKeysMaterial);
		padlockFinder = Util.createItem(config.padlockFinderName, Material.COMPASS);
	}

	/**
	 * Creates a recipe for an item with defaults ingredients.
	 *
	 * @param result The item.
	 * @param shape The shape.
	 */

	public void createRecipe(final ItemStack result, final List<String> shape) {
		createRecipe(result, shape, plugin.getPluginConfig().shapeMaterials);
	}

	/**
	 * Creates a recipe for an item.
	 *
	 * @param result The item.
	 * @param shape The shape.
	 * @param ingredients The ingredients needed for the craft.
	 */

	public void createRecipe(final ItemStack result, final List<String> shape, Map<String, String> ingredients) {
		final ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, ChatColor.stripColor(result.getItemMeta().getDisplayName()).toLowerCase().replace(' ', '_') + "_" + result.getAmount()), result);
		recipe.shape(shape.toArray(new String[0]));
		if(ingredients.equals(plugin.getPluginConfig().shapeMaterials)) {
			ingredients = Util.keepAll(ingredients, shape);
		}
		for(final Map.Entry<String, String> entry : ingredients.entrySet()) {
			recipe.setIngredient(entry.getKey().charAt(0), Material.valueOf(entry.getValue()));
		}
		Bukkit.addRecipe(recipe);
	}
	
	/**
	 * Returns a clone of the key item.
	 * 
	 * @return A clone of the key item.
	 */
	
	public ItemStack getKeyItem() {
		return key.clone();
	}

	/**
	 * Sets the key item.
	 *
	 * @param key The key item.
	 */

	public void setKeyItem(final ItemStack key) {
		this.key = key;
	}

	/**
	 * Returns a clone of the master key item.
	 * 
	 * @return A clone of the master key item.
	 */
	
	public ItemStack getMasterKeyItem() {
		return masterKey.clone();
	}

	/**
	 * Sets the master key item.
	 *
	 * @param masterKey The master key item.
	 */

	public void setMasterKeyItem(final ItemStack masterKey) {
		this.masterKey = masterKey;
	}

	/**
	 * Returns the key clone item.
	 * 
	 * @return The key clone item.
	 */
	
	public ItemStack getKeyCloneItem() {
		return keyClone.clone();
	}

	/**
	 * Sets the key clone item.
	 *
	 * @param keyClone The key clone item.
	 */

	public void setKeyCloneItem(final ItemStack keyClone) {
		this.keyClone = keyClone;
	}

	/**
	 * Returns the bunch of keys item.
	 *
	 * @return The bunch of keys item.
	 */

	public ItemStack getBunchOfKeysItem() {
		return bunchOfKeys;
	}

	/**
	 * Sets the bunch of keys item.
	 *
	 * @param bunchOfKeys The bunch of keys item.
	 */

	public void setBunchOfKeysItem(final ItemStack bunchOfKeys) {
		this.bunchOfKeys = bunchOfKeys;
	}

	/**
	 * Returns the padlock finder item.
	 * 
	 * @return The padlock finder item.
	 */
	
	public ItemStack getPadlockFinderItem() {
		return padlockFinder.clone();
	}

	/**
	 * Sets the padlock finder item.
	 *
	 * @param padlockFinder The padlock finder item.
	 */

	public void setPadlockFinderItem(final ItemStack padlockFinder) {
		this.padlockFinder = padlockFinder;
	}
	
	/**
	 * Creates a padlock for the selected location.
	 * 
	 * @param location The location.
	 */
	
	public void createPadlock(final Location location) {
		createPadlock(location, null);
	}
	
	/**
	 * Creates a padlock for the selected location.
	 * <br>It will format a key too.
	 * 
	 * @param location The location (can be corrected if needed).
	 * @param key The key.
	 */
	
	public void createPadlock(final Location location, final ItemStack key) {
		correctLocation(location);
		plugin.getPluginData().padlocks.add(location);
		if(isBlankKey(key)) {
			formatItem(location, key);
		}
	}
	
	/**
	 * Removes a padlock.
	 * 
	 * @param location The location (can be corrected if needed).
	 */
	
	public void removePadlock(final Location location) {
		removePadlock(location, null);
	}
	
	/**
	 * Removes a padlock.
	 * <br>It will format a key too.
	 * 
	 * @param location The location (can be corrected if needed).
	 * @param key The key.
	 */
	
	public void removePadlock(final Location location, final ItemStack key) {
		correctLocation(location);
		plugin.getPluginData().padlocks.remove(location);
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
	
	public boolean hasPadlock(final Location location) {
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
	
	public boolean hasPadlock(final Location location, final boolean correctLocation) {
		if(correctLocation) {
			correctLocation(location);
		}
		return plugin.getPluginData().padlocks.contains(location);
	}
	
	/**
	 * Checks if the specified item is a key (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isKey(final ItemStack item) {
		return Util.isValidItem(item) && item.getType() == key.getType() && (plugin.getPluginConfig().canRenameItems || item.getItemMeta().getDisplayName().equals(key.getItemMeta().getDisplayName()));
	}
	
	/**
	 * Checks if the specified item is a blank key.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isBlankKey(final ItemStack item) {
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
	
	public boolean isUsedKey(final ItemStack item) {
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
	
	public boolean isMasterKey(final ItemStack item) {
		return Util.isValidItem(item) && item.getType() == masterKey.getType() && (plugin.getPluginConfig().canRenameItems || item.getItemMeta().getDisplayName().equals(masterKey.getItemMeta().getDisplayName()));
	}
	
	/**
	 * Checks if the specified item is a bunch of keys (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isBunchOfKeys(final ItemStack item) {
		return Util.isValidItem(item) && item.getType() == bunchOfKeys.getType() && (plugin.getPluginConfig().canRenameItems || item.getItemMeta().getDisplayName().equals(bunchOfKeys.getItemMeta().getDisplayName()));
	}
	
	/**
	 * Checks if the specified inventory is a bunch of keys (blank or used).
	 * 
	 * @param inventory The inventory.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isBunchOfKeys(final Inventory inventory) {
		return inventory.getName().equals(bunchOfKeys.getItemMeta().getDisplayName()) && inventory.getSize() == 9;
	}
	
	/**
	 * Checks if the specified item is a blank bunch of keys.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isBlankBunchOfKeys(final ItemStack item) {
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
	
	public boolean isUsedBunchOfKeys(final ItemStack item) {
		return isBunchOfKeys(item) && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() % 2 == 0;
	}
	
	/**
	 * Checks if the specified item is a padlock finder (blank or used).
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isPadlockFinder(final ItemStack item) {
		return Util.isValidItem(item) && item.getType() == Material.COMPASS && item.getItemMeta().getDisplayName().equals(padlockFinder.getItemMeta().getDisplayName());
	}
	
	/**
	 * Checks if the specified item is a blank padlock finder.
	 * 
	 * @param item The item.
	 * 
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */
	
	public boolean isBlankPadlockFinder(final ItemStack item) {
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
	
	public boolean isUsedPadlockFinder(final ItemStack item) {
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
	 */
	
	public boolean isValidKey(final ItemStack key, final Location location) {
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
	 */
	
	public boolean isValidKey(final ItemStack key, final Location location, final Player player) {
		if(isMasterKey(key)) {
			if(player != null && !player.hasPermission("serialkey.use.masterkey")) {
				plugin.sendMessage(player, plugin.getPluginMessages().messagePermission);
			}
			return true;
		}
		correctLocation(location);
		try {
			final Location keyLocation = extractLocation(key);
			if(keyLocation != null && keyLocation.equals(location)) {
				if(player != null && !player.hasPermission("serialkey.use.key")) {
					plugin.sendMessage(player, plugin.getPluginMessages().messagePermission);
				}
				return true;
			}
			final ItemStack[] extractedKeys = extractKeys(key);
			if(extractedKeys != null) {
				if(player != null && !player.hasPermission("serialkey.use.bunchofkeys")) {
					plugin.sendMessage(player, plugin.getPluginMessages().messagePermission);
					return true;
				}
				for(final ItemStack extractedKey : extractedKeys) {
					if(isValidKey(extractedKey, location, null)) {
						return true;
					}
				}
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Extracts a location from a key or a padlock finder.
	 * 
	 * @param item The item.
	 * 
	 * @return The location.
	 */
	
	public Location extractLocation(final ItemStack item) {
		boolean isKey = isUsedKey(item);
		if(!isKey && !isUsedPadlockFinder(item)) {
			return null;
		}
		final List<String> lore = item.getItemMeta().getLore();
		String loreWorld = ChatColor.stripColor(lore.get(0));
		String loreLocation = ChatColor.stripColor(lore.get(1));
		if(plugin.getPluginConfig().encryptLore) {
			loreWorld = ROT47.rotate(loreWorld);
			loreLocation = ROT47.rotate(loreLocation);
		}
		final World world = Bukkit.getWorld(loreWorld);
		if(world == null) {
			return null;
		}
		final String[] rawLocation = loreLocation.split(", ");
		if(rawLocation.length != 3) {
			return null;
		}
		final Location itemLocation = new Location(world, Integer.parseInt(rawLocation[0]), Integer.parseInt(rawLocation[1]), Integer.parseInt(rawLocation[2]));
		if(isKey && correctLocation(itemLocation)) {
			formatItem(itemLocation, item);
		}
		return itemLocation;
	}
	
	/**
	 * Returns a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * 
	 * @return The key.
	 */
	
	public ItemStack getKey(final Location location) {
		final ItemStack key = getKeyItem();
		formatItem(location, key);
		return key;
	}
	
	/**
	 * Returns a padlock finder for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * 
	 * @return The padlock finder.
	 */
	
	public ItemStack getPadlockFinder(final Location location) {
		final ItemStack padlockFinder = getPadlockFinderItem();
		formatItem(location, padlockFinder);
		return padlockFinder;
	}
	
	/**
	 * Formats a key for the specified location.
	 * 
	 * @param location The location (will not be corrected).
	 * @param item The key.
	 */
	
	public void formatItem(final Location location, final ItemStack item) {
		if(!isKey(item) && !isPadlockFinder(item)) {
			return;
		}
		final ChatColor color = Util.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC, ChatColor.BLACK);
		final ItemMeta meta = item.getItemMeta();
		String loreWorld = location.getWorld().getName();
		String loreLocation = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
		if(plugin.getPluginConfig().encryptLore) {
			loreWorld = ROT47.rotate(loreWorld);
			loreLocation = ROT47.rotate(loreLocation);
		}
		meta.setLore(Arrays.asList(color + loreWorld, color + loreLocation));
		item.setItemMeta(meta);
	}
	
	/**
	 * Adds a key to a bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys.
	 * @param key The key.
	 */
	
	public void addKey(final ItemStack bunchOfKeys, final ItemStack key) {
		if(!isBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
			return;
		}
		final ItemMeta meta = bunchOfKeys.getItemMeta();
		final List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
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
	
	public short removeKey(final ItemStack bunchOfKeys, final ItemStack key) {
		if(!isUsedBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
			return 0;
		}

		final ItemMeta meta = bunchOfKeys.getItemMeta();
		final List<String> lore = meta.getLore();
		final List<String> keyLore = key.getItemMeta().getLore();

		for(final String line : new ArrayList<>(keyLore)) {
			keyLore.remove(line);
			keyLore.add(ChatColor.stripColor(line));
		}

		short deleted = 0;
		final int n = lore.size();
		for(int i = 0; i != n; i++) {
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
	
	public void clearKeys(final ItemStack bunchOfKeys) {
		if(!isUsedBunchOfKeys(bunchOfKeys)) {
			return;
		}
		final ItemMeta meta = bunchOfKeys.getItemMeta();
		meta.setLore(null);
		bunchOfKeys.setItemMeta(meta);
	}
	
	/**
	 * Extracts all keys from a bunch of keys.
	 * 
	 * @param bunchOfKeys The bunch of keys.
	 * 
	 * @return The keys.
	 */
	
	public ItemStack[] extractKeys(final ItemStack bunchOfKeys) {
		if(!isUsedBunchOfKeys(bunchOfKeys)) {
			return null;
		}
		final List<String> lore = bunchOfKeys.getItemMeta().getLore();
		final List<ItemStack> keys = new ArrayList<>();

		final int n = lore.size();
		for(int i = 0; i != n; i++) {
			final ItemStack blankKey = getKeyItem();
			final ItemMeta blankMeta = blankKey.getItemMeta();
			blankMeta.setLore(Arrays.asList(lore.get(i), lore.get(++i)));
			blankKey.setItemMeta(blankMeta);
			keys.add(blankKey);
		}

		return keys.toArray(new ItemStack[0]);
	}
	
	/**
	 * Creates an inventory for the specified bunch of keys and opens it for the specified players.
	 * 
	 * @param bunchOfKeys The bunch of keys item.
	 * @param players The players.
	 * 
	 * @return The inventory.
	 */
	
	public Inventory createInventory(final ItemStack bunchOfKeys, final Player... players) {
		if(!isBunchOfKeys(bunchOfKeys)) {
			return null;
		}

		final Inventory inventory = Bukkit.createInventory(null, 9, bunchOfKeys.getItemMeta().getDisplayName());
		inventory.setMaxStackSize(1);

		final ItemStack[] keys = extractKeys(bunchOfKeys);
		if(keys != null && keys.length != 0) {
			inventory.addItem(keys);
		}

		if(players != null) {
			for(final Player player : players) {
				player.openInventory(inventory);
			}
		}

		return inventory;
	}

	/**
	 * Corrects a location (used to handle doors because they are composed from two blocks and double chests too).
	 *
	 * @param location The location.
	 *
	 * @return <b>true :</b> If the location has been corrected.
	 * <br /><b>false :</b> Otherwise.
	 */

	public boolean correctLocation(final Location location) {
		if(hasPadlock(location, false)) {
			return false;
		}

		final Block block = location.getBlock();
		final BlockData data = block.getBlockData();
		if(data instanceof Chest) {
			final InventoryHolder holder = ((org.bukkit.block.Chest)block.getState()).getInventory().getHolder();
			if(holder instanceof DoubleChest) {
				final Location left = ((org.bukkit.block.Chest)((DoubleChest)holder).getLeftSide()).getLocation();
				if(hasPadlock(left, false)) {
					location.setX(left.getX());
					location.setZ(left.getZ());
					return true;
				}

				final Location right = ((org.bukkit.block.Chest)((DoubleChest)holder).getRightSide()).getLocation();
				if(hasPadlock(right, false)) {
					location.setX(right.getX());
					location.setZ(right.getZ());
					return true;
				}
			}
			return false;
		}

		if(DoorUtil.getInstance(data) != null) {
			location.setY(DoorUtil.getBlockBelow(block).getY());
			if(hasPadlock(location, false)) {
				return true;
			}
			final Block doubleDoor = DoorUtil.getDoubleDoor(block);
			if(doubleDoor != null) {
				final Location doubleDoorLocation = doubleDoor.getLocation();
				location.setX(doubleDoorLocation.getX());
				location.setZ(doubleDoorLocation.getZ());
			}
			return true;
		}

		return false;
	}
	
}