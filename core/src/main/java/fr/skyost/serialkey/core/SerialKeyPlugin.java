package fr.skyost.serialkey.core;

import fr.skyost.serialkey.core.config.SerialKeyConfig;
import fr.skyost.serialkey.core.config.SerialKeyMessages;
import fr.skyost.serialkey.core.item.PluginItemManager;
import fr.skyost.serialkey.core.object.SerialKeyPerson;
import fr.skyost.serialkey.core.padlock.PluginPadlockManager;
import fr.skyost.serialkey.core.unlocker.PluginUnlocker;

/**
 * Represents a SerialKey plugin.
 *
 * @param <I> The ItemStack class.
 * @param <L> The Location class.
 */

public interface SerialKeyPlugin<I, L> {

	/**
	 * Returns the item manager.
	 *
	 * @return The item manager.
	 */

	PluginItemManager<I> getItemManager();

	/**
	 * Returns the unlocker.
	 *
	 * @return The unlocker.
	 */

	PluginUnlocker<I> getUnlocker();

	/**
	 * Returns the padlock manager.
	 *
	 * @return The padlock manager.
	 */

	PluginPadlockManager<I, L> getPadlockManager();

	/**
	 * Returns the plugin configuration.
	 *
	 * @return The plugin configuration.
	 */

	SerialKeyConfig getPluginConfig();

	/**
	 * Returns the plugin messages.
	 *
	 * @return The plugin messages.
	 */

	SerialKeyMessages getPluginMessages();

	/**
	 * Sends a message with the plugin prefix.
	 *
	 * @param person Who receives the message.
	 * @param message The message.
	 */

	default void sendMessage(final SerialKeyPerson person, final String message) {
		person.sendMessage(getPluginMessages().getPrefix() + " " + message);
	}

}