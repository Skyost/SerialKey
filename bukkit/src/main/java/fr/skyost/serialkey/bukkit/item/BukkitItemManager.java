package fr.skyost.serialkey.bukkit.item;

import fr.skyost.serialkey.bukkit.SerialKey;
import fr.skyost.serialkey.bukkit.config.BukkitPluginConfig;
import fr.skyost.serialkey.bukkit.util.Util;
import fr.skyost.serialkey.core.item.PluginItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The Bukkit item manager class.
 */

public class BukkitItemManager extends PluginItemManager<ItemStack> {

	/**
	 * The plugin instance.
	 */

	private final SerialKey plugin;

	/**
	 * Creates a new Bukkit item manager instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public BukkitItemManager(final SerialKey plugin) {
		this(plugin, plugin.getPluginConfig(), Util.createItem(plugin.getPluginConfig().keyName, plugin.getPluginConfig().keyMaterial));
	}

	/**
	 * Creates a new Bukkit item manager instance.
	 *
	 * @param plugin The plugin instance.
	 * @param key The key item.
	 */

	private BukkitItemManager(final SerialKey plugin, final BukkitPluginConfig config, final ItemStack key) {
		super(plugin.getPluginConfig(), key, Util.createItem(config.masterKeyName, config.masterKeyMaterial), key.clone(), Util.createItem(config.bunchOfKeysName, config.bunchOfKeysMaterial), Util.createItem(config.padlockFinderName, Material.COMPASS));

		this.plugin = plugin;
		getKeyCloneItem().setAmount(2);
	}

	@Override
	public void createRecipe(final String id, final ItemStack result, final List<String> shape, Map<String, String> ingredients) {
		final ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, id), result);
		recipe.shape(shape.toArray(new String[0]));
		if(ingredients.equals(config.getShapeMaterials())) {
			ingredients = fr.skyost.serialkey.core.util.Util.keepAll(ingredients, shape);
		}
		for(final Map.Entry<String, String> entry : ingredients.entrySet()) {
			recipe.setIngredient(entry.getKey().charAt(0), Material.valueOf(entry.getValue()));
		}
		Bukkit.addRecipe(recipe);
	}

	@Override
	protected boolean isItemValid(final ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
	}

	@Override
	protected boolean compareItemsName(final ItemStack item1, final ItemStack item2) {
		return item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName());
	}

	@Override
	protected boolean compareItemsType(final ItemStack item1, final ItemStack item2) {
		return item1.getType() == item2.getType();
	}

	@Override
	public List<String> getLore(final ItemStack object) {
		if(object == null) {
			return new ArrayList<>();
		}

		final ItemMeta meta = object.getItemMeta();
		if(meta == null) {
			return new ArrayList<>();
		}

		final List<String> lore = meta.getLore();
		return lore == null ? new ArrayList<>() : lore;
	}

	@Override
	public void setLore(final ItemStack object, final List<String> lore) {
		final ItemMeta meta = object.getItemMeta();
		meta.setLore(lore);
		object.setItemMeta(meta);
	}

	/**
	 * Checks if the specified inventory is a bunch of keys (blank or used).
	 *
	 * @param event The inventory event.
	 *
	 * @return <b>true :</b> yes.
	 * <br><b>false :</b> no.
	 */

	public boolean isBunchOfKeys(final InventoryEvent event) {
		final ItemStack bunchOfKeys = getBunchOfKeysItem();
		return event.getView().getTitle().equals(bunchOfKeys.getItemMeta().getDisplayName()) && event.getInventory().getSize() == 9;
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
		final Inventory inventory = Bukkit.createInventory(null, 9, bunchOfKeys.getItemMeta().getDisplayName());
		inventory.setMaxStackSize(1);

		final List<String> lore = getLore(bunchOfKeys);
		final int n = lore.size();
		for(int i = 0; i < n; i++) {
			final ItemStack key = getKeyItem().clone();
			final ItemMeta meta = key.getItemMeta();
			meta.setLore(Arrays.asList(lore.get(i), lore.get(++i)));
			key.setItemMeta(meta);
			inventory.addItem(key);
		}

		/*final List<SerialKeyLocation> locations = unlocker.getLocations(bunchOfKeys);
		if(!locations.isEmpty()) {
			for(final SerialKeyLocation object : locations) {
				final ItemStack key = getKeyItem().clone();
				unlocker.addLocation(key, object);
				inventory.addItem(key);
			}
		}*/

		if(players != null) {
			for(final Player player : players) {
				player.openInventory(inventory);
			}
		}

		return inventory;
	}

}