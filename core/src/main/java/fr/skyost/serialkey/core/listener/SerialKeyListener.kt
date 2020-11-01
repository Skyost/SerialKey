package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.item.PluginItemManager
import fr.skyost.serialkey.core.padlock.PluginPadlockManager
import fr.skyost.serialkey.core.unlocker.PluginUnlocker

/**
 * Represents a SerialKey listener.
 *
 * @param <I> ItemStack class.
 * @param <L> Location class.
 */
open class SerialKeyListener<I, L> internal constructor(plugin: SerialKeyPlugin<I, L>) {
    var plugin: SerialKeyPlugin<I, L> = plugin
    set(value) {
        field = value
        itemManager = plugin.itemManager
        padlockManager = plugin.padlockManager
        unlocker = plugin.unlocker
    }

    /**
     * The item manager.
     */
    protected lateinit var itemManager: PluginItemManager<I>

    /**
     * The padlock manager.
     */
    protected lateinit var padlockManager: PluginPadlockManager<I, L>

    /**
     * The unlocker.
     */
    protected lateinit var unlocker: PluginUnlocker<I>
}