package fr.skyost.serialkey;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.skyost.serialkey.listeners.EventsListener;
import fr.skyost.serialkey.utils.Skyupdater;

public class SerialKey extends JavaPlugin {
	
	protected static ItemStack key;
	protected static ItemStack masterKey;
	protected static ItemStack keyClone;
	
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
			createRecipe(key, config.keyShape, config.keyShapeMaterials);
			masterKey = createItem(config.masterKeyName, config.masterKeyMaterial);
			createRecipe(masterKey, config.masterKeyShape, config.masterKeyShapeMaterials);
			keyClone = key.clone();
			keyClone.setAmount(2);
			createRecipe(keyClone, Arrays.asList("EE"), new HashMap<String, String>() {
				private static final long serialVersionUID = 1L; {
					put("E", config.keyMaterial.name());
				}
			});
			Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
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
	 * Creates a recipe for an item.
	 * 
	 * @param result The item.
	 * @param shape The shape.
	 * @param ingredients The ingredients needed for the craft.
	 */
	
	private final void createRecipe(final ItemStack result, final List<String> shape, final Map<String, String> ingredients) {
		final ShapedRecipe recipe = new ShapedRecipe(result);
		recipe.shape(shape.toArray(new String[shape.size()]));
		for(final Entry<String, String> entry : ingredients.entrySet()) {
			recipe.setIngredient(entry.getKey().charAt(0), Material.valueOf(entry.getValue()));
		}
		Bukkit.addRecipe(recipe);
	}
	
}
