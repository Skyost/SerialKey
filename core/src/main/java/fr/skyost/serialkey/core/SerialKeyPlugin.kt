package fr.skyost.serialkey.core

import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import fr.skyost.serialkey.core.config.SerialKeyConfig
import fr.skyost.serialkey.core.config.SerialKeyMessages
import fr.skyost.serialkey.core.item.PluginItemManager
import fr.skyost.serialkey.core.padlock.PluginPadlockManager
import fr.skyost.serialkey.core.unlocker.PluginUnlocker

/**
 * Represents a SerialKey plugin.
 *
 * @param <I> The ItemStack class.
 * @param <L> The Location class.
 */
interface SerialKeyPlugin<I, L> {
    /**
     * Returns the item manager.
     *
     * @return The item manager.
     */
    val itemManager: PluginItemManager<I>

    /**
     * Returns the unlocker.
     *
     * @return The unlocker.
     */
    val unlocker: PluginUnlocker<I>

    /**
     * Returns the padlock manager.
     *
     * @return The padlock manager.
     */
    val padlockManager: PluginPadlockManager<I, L>

    /**
     * Returns the plugin configuration.
     *
     * @return The plugin configuration.
     */
    val pluginConfig: SerialKeyConfig

    /**
     * Returns the plugin messages.
     *
     * @return The plugin messages.
     */
    val pluginMessages: SerialKeyMessages

    /**
     * Sends a message with the plugin prefix.
     *
     * @param person Who receives the message.
     * @param message The message.
     */
    fun sendMessage(person: SerialKeyPerson, message: String) {
        person.sendMessage(pluginMessages.prefix + " " + message)
    }
}