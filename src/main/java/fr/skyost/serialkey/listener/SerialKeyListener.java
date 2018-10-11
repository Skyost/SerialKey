package fr.skyost.serialkey.listener;

import org.bukkit.event.Listener;

import fr.skyost.serialkey.SerialKey;
import fr.skyost.serialkey.SerialKeyAPI;

/**
 * A class that represents a SerialKey listener.
 */

public abstract class SerialKeyListener implements Listener {

	/**
	 * The plugin instance.
	 */

	SerialKey plugin;

	/**
	 * The plugin API.
	 */

	SerialKeyAPI api;

	/**
	 * Creates a new SerialKey listener instance.
	 *
	 * @param plugin The plugin.
	 */

	SerialKeyListener(final SerialKey plugin) {
		setPlugin(plugin);
	}

	/**
	 * Returns the plugin instance.
	 *
	 * @return The plugin instance.
	 */

	public SerialKey getPlugin() {
		return plugin;
	}

	/**
	 * Sets the plugin instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public void setPlugin(final SerialKey plugin) {
		this.plugin = plugin;
		this.api = plugin.getAPI();
	}

}