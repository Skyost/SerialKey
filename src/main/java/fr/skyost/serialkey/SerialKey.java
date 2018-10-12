package fr.skyost.serialkey;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import fr.skyost.serialkey.command.SerialKeyCommand;
import fr.skyost.serialkey.config.PluginConfig;
import fr.skyost.serialkey.config.PluginData;
import fr.skyost.serialkey.config.PluginMessages;
import fr.skyost.serialkey.listener.BlocksListener;
import fr.skyost.serialkey.listener.BunchOfKeysListener;
import fr.skyost.serialkey.listener.GlobalListener;
import fr.skyost.serialkey.listener.HopperListener;
import fr.skyost.serialkey.listener.LostChestsListener;
import fr.skyost.serialkey.listener.PadlockFinderListener;
import fr.skyost.serialkey.util.Skyupdater;
import fr.skyost.serialkey.util.Util;

/**
 * The SerialKey plugin class.
 */

public class SerialKey extends JavaPlugin {

	/**
	 * The plugin config.
	 */
	
	private PluginConfig config;

	/**
	 * The plugin messages.
	 */

	private PluginMessages messages;

	/**
	 * The plugin data.
	 */

	private PluginData data;

	/**
	 * The plugin API.
	 */

	private SerialKeyAPI api;
	
	@Override
	public final void onEnable() {
		try {
			final File dataFolder = this.getDataFolder();
			
			// Configuration :
			
			config = new PluginConfig(dataFolder);
			config.load();
			messages = new PluginMessages(dataFolder);
			messages.load();
			data = new PluginData(dataFolder);
			data.load();
			handleLocations();
			
			// Items :

			api = new SerialKeyAPI(this);
			api.createRecipe(api.getKeyItem(), config.keyShape);
			api.createRecipe(api.getMasterKeyItem(), config.masterKeyShape);
			api.createRecipe(api.getKeyCloneItem(), Collections.singletonList("YY"), Objects.requireNonNull(Util.createMap(new String[]{"Y"}, new String[]{config.keyMaterial.name()})));
			api.createRecipe(api.getBunchOfKeysItem(), config.bunchOfKeysShape);
			api.createRecipe(api.getPadlockFinderItem(), Collections.singletonList("ZY"), Objects.requireNonNull(Util.createMap(new String[]{"Z", "Y"}, new String[]{Material.COMPASS.name(), config.keyMaterial.name()})));
			
			// Events :
			
			final PluginManager manager = Bukkit.getPluginManager();
			manager.registerEvents(new GlobalListener(this), this);
			manager.registerEvents(new BlocksListener(this), this);
			manager.registerEvents(new BunchOfKeysListener(this), this);
			manager.registerEvents(new PadlockFinderListener(this), this);
			if(config.disableHoppers) {
				manager.registerEvents(new HopperListener(this), this);
			}
			if(!config.allowLostChests) {
				manager.registerEvents(new LostChestsListener(this), this);
			}
			
			// Commands :
			
			final SerialKeyCommand executor = new SerialKeyCommand(this);
			final PluginCommand command = this.getCommand("serialkey");
			command.setUsage(executor.getUsage());
			command.setExecutor(executor);
			
			// Services :
			
			if(config.enableUpdater) {
				new Skyupdater(this, 84423, this.getFile(), true, true);
			}
			if(config.enableMetrics) {
				new MetricsLite(this);
			}
		}
		catch(final NullPointerException ex) {
			sendMessage(Bukkit.getConsoleSender(), ChatColor.RED + "Null pointer exception ! Maybe you have misconfigured one (or more) item shape.");
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
	 * Returns the plugin's config.
	 *
	 * @return The plugin's config.
	 */

	public PluginConfig getPluginConfig() {
		return config;
	}

	/**
	 * Returns the plugin's messages.
	 *
	 * @return The plugin's messages.
	 */

	public PluginMessages getPluginMessages() {
		return messages;
	}

	/**
	 * Returns the plugin's data.
	 *
	 * @return The plugin's data.
	 */

	public PluginData getPluginData() {
		return data;
	}

	/**
	 * Returns the plugin's API.
	 *
	 * @return The plugin's API.
	 */

	public SerialKeyAPI getAPI() {
		return api;
	}

	/**
	 * Sends a message with the plugin's prefix.
	 *
	 * @param sender Who receives the message.
	 * @param message The message.
	 */

	public void sendMessage(final CommandSender sender, final String message) {
		sender.sendMessage(messages.prefix + " " + message);
	}

	/**
	 * A little trick for the Skyoconfig because it cannot handle parameterized types.
	 */
	
	private void handleLocations() {
		for(final Object object : new ArrayList<Object>(data.padlocks)) {
			final JSONObject json = (JSONObject)JSONValue.parse(object.toString());
			data.padlocks.add(new Location(Bukkit.getWorld(json.get("world").toString()), Double.parseDouble(json.get("x").toString()), Double.parseDouble(json.get("y").toString()), Double.parseDouble(json.get("z").toString()), Float.parseFloat(json.get("yaw").toString()), Float.parseFloat(json.get("pitch").toString())));
			data.padlocks.remove(object);
		}
	}
	
}