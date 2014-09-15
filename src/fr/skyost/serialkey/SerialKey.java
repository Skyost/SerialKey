package fr.skyost.serialkey;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.skyost.serialkey.commands.SerialKeyCommand;
import fr.skyost.serialkey.listeners.*;
import fr.skyost.serialkey.utils.Skyupdater;
import fr.skyost.serialkey.utils.Utils;

public class SerialKey extends JavaPlugin {
	
	protected static ItemStack key;
	protected static ItemStack masterKey;
	protected static ItemStack keyClone;
	protected static ItemStack bunchOfKeys;
	protected static ItemStack padlockFinder;
	
	protected static PluginConfig config;
	protected static PluginMessages messages;
	protected static PluginData data;
	
	@Override
	public final void onEnable() {
		try {
			final File dataFolder = this.getDataFolder();
			config = new PluginConfig(dataFolder);
			config.load();
			messages = new PluginMessages(dataFolder);
			messages.load();
			data = new PluginData(dataFolder);
			data.load();
			handleLocations();
			key = createItem(config.keyName, config.keyMaterial);
			createRecipe(key, config.keyShape);
			masterKey = createItem(config.masterKeyName, config.masterKeyMaterial);
			createRecipe(masterKey, config.masterKeyShape);
			keyClone = key.clone();
			keyClone.setAmount(2);
			createRecipe(keyClone, Arrays.asList("YY"), createMap(new String[]{"Y"}, new String[]{config.keyMaterial.name()}));
			bunchOfKeys = createItem(config.bunchOfKeysName, config.bunchOfKeysMaterial);
			createRecipe(bunchOfKeys, config.bunchOfKeysShape);
			padlockFinder = createItem(config.padlockFinderName, Material.COMPASS);
			createRecipe(padlockFinder, Arrays.asList("ZY"), createMap(new String[]{"Z", "Y"}, new String[]{Material.COMPASS.name(), config.keyMaterial.name()}));
			final PluginManager manager = Bukkit.getPluginManager();
			manager.registerEvents(new GlobalListener(), this);
			manager.registerEvents(new BlocksListener(), this);
			manager.registerEvents(new BunchOfKeysListener(), this);
			manager.registerEvents(new PadlockFinderListener(), this);
			final SerialKeyCommand executor = new SerialKeyCommand();
			final PluginCommand command = this.getCommand("serialkey");
			command.setUsage(executor.getUsage());
			command.setExecutor(executor);
			if(config.enableUpdater) {
				new Skyupdater(this, 84423, this.getFile(), true, true);
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public final void onDisable() {
		try {
			data.save();
			clearFields();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A little trick for the Skyoconfig because it cannot handle parametized types.
	 */
	
	private final void handleLocations() {
		for(final Object object : new ArrayList<Object>(data.padlocks)) {
			final JSONObject json = (JSONObject)JSONValue.parse(object.toString());
			data.padlocks.add(new Location(Bukkit.getWorld(json.get("world").toString()), Double.parseDouble(json.get("x").toString()), Double.parseDouble(json.get("y").toString()), Double.parseDouble(json.get("z").toString()), Float.parseFloat(json.get("yaw").toString()), Float.parseFloat(json.get("pitch").toString())));
			data.padlocks.remove(object);
		}
	}
	
	/**
	 * Creates an item with a custom name.
	 * 
	 * @param name The name.
	 * @param material The item's material.
	 * 
	 * @return The item.
	 */
	
	private final ItemStack createItem(final String name, final Material material) {
		final ItemStack item = new ItemStack(material);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * Creates a recipe for an item with defaults ingredients.
	 * 
	 * @param result The item.
	 * @param shape The shape.
	 */
	
	private final void createRecipe(final ItemStack result, final List<String> shape) {
		createRecipe(result, shape, config.shapeMaterials);
	}
	
	/**
	 * Creates a recipe for an item.
	 * 
	 * @param result The item.
	 * @param shape The shape.
	 * @param ingredients The ingredients needed for the craft.
	 */
	
	private final void createRecipe(final ItemStack result, final List<String> shape, Map<String, String> ingredients) {
		final ShapedRecipe recipe = new ShapedRecipe(result);
		recipe.shape(shape.toArray(new String[shape.size()]));
		if(ingredients.equals(config.shapeMaterials)) {
			ingredients = Utils.keepAll(ingredients, shape);
		}
		for(final Entry<String, String> entry : ingredients.entrySet()) {
			recipe.setIngredient(entry.getKey().charAt(0), Material.valueOf(entry.getValue()));
		}
		Bukkit.addRecipe(recipe);
	}
	
	/**
	 * Creates a map.
	 * 
	 * @param key The keys.
	 * @param value The values.
	 * 
	 * @return The map.
	 */
	
	private final <V, K> Map<K, V> createMap(final K[] keys, final V[] values) {
		if(keys.length != values.length) {
			return null;
		}
		final Map<K, V> map = new HashMap<K, V>();
		for(int i = 0; i != keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}
	
	/**
	 * Used to avoid memory leaks.
	 * 
	 * @throws IllegalArgumentException Can never happen.
	 * @throws IllegalAccessException Same here.
	 */
	
	private final void clearFields() throws IllegalArgumentException, IllegalAccessException {
		for(final Field field : this.getClass().getDeclaredFields()) {
			field.set(null, null);
		}
	}
	
}
