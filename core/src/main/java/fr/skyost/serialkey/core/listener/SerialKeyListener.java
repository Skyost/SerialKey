package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.item.PluginItemManager;
import fr.skyost.serialkey.core.padlock.PluginPadlockManager;
import fr.skyost.serialkey.core.unlocker.PluginUnlocker;

/**
 * Represents a SerialKey listener.
 *
 * @param <I> ItemStack class.
 * @param <L> Location class.
 */

public class SerialKeyListener<I, L> {

	/**
	 * The plugin.
	 */

	protected SerialKeyPlugin plugin;

	/**
	 * The item manager.
	 */

	protected PluginItemManager<I> itemManager;

	/**
	 * The padlock manager.
	 */

	protected PluginPadlockManager<I, L> padlockManager;

	/**
	 * The unlocker.
	 */

	protected PluginUnlocker<I> unlocker;

	/**
	 * Creates a new SerialKey listener instance.
	 *
	 * @param plugin The plugin.
	 */

	SerialKeyListener(final SerialKeyPlugin<I, L> plugin) {
		setPlugin(plugin);
	}

	/**
	 * Returns the plugin instance.
	 *
	 * @return The plugin instance.
	 */

	public SerialKeyPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Sets the plugin instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public void setPlugin(final SerialKeyPlugin<I, L> plugin) {
		this.plugin = plugin;

		itemManager = plugin.getItemManager();
		padlockManager = plugin.getPadlockManager();
		unlocker = plugin.getUnlocker();
	}

}