package fr.skyost.serialkey.core.padlock

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.config.SerialKeyData

/**
 * A padlock manager that depends on a plugin.
 *
 * @param <I> ItemStack class.
 * @param <L> Location class.
 */
abstract class PluginPadlockManager<I, L>(private val plugin: SerialKeyPlugin<I, L>) : PadlockManager() {
    /**
     * Loads the padlocks from the specified data.
     *
     * @param data The data.
     */
    fun load(data: SerialKeyData) {
        val locations: Collection<SerialKeyLocation> = data.getPadlocks()
        for (location in locations) {
            registerPadlock(location)
        }
    }

    /**
     * Saves the padlocks to the specified data.
     *
     * @param data The data.
     */
    fun save(data: SerialKeyData) {
        for (location in getPadlocks()) {
            data.addPadlock(location)
        }
    }
    /**
     * Registers a padlock at the specified location.
     *
     * @param location The location.
     * @param item The item to format.
     */
    @JvmOverloads
    fun registerPadlock(location: L, item: I? = null) {
        registerPadlock(pluginLocationToSerialKeyLocation(location), item)
    }

    /**
     * Registers a padlock at the specified location.
     *
     * @param location The location.
     * @param item The item to format.
     */
    fun registerPadlock(location: SerialKeyLocation, item: I?) {
        super.registerPadlock(location)
        if (item != null && plugin.itemManager.isBlankKey(item)) {
            plugin.unlocker.setLocation(item, location)
        }
    }

    /**
     * Returns whether or no there is a padlock at the specified location.
     *
     * @param location The location.
     *
     * @return Whether or no there is a padlock at the specified location.
     */
    fun hasPadlock(location: L): Boolean {
        return hasPadlock(pluginLocationToSerialKeyLocation(location))
    }

    /**
     * Returns whether or no there is a padlock at the specified location.
     *
     * @param location The location.
     * @param fixLocation Whether the location should be fixed.
     *
     * @return Whether or no there is a padlock at the specified location.
     */
    @JvmOverloads
    fun hasPadlock(location: SerialKeyLocation, fixLocation: Boolean = true): Boolean {
        if (fixLocation) {
            fixLocation(location)
        }
        return super.hasPadlock(location)
    }

    /**
     * Unregisters a padlock located at the specified location.
     *
     * @param location The location.
     */
    override fun unregisterPadlock(location: SerialKeyLocation) {
        unregisterPadlock(location, null)
    }

    /**
     * Unregisters a padlock located at the specified location.
     *
     * @param location The location.
     * @param item The item to format.
     */
    private fun unregisterPadlock(location: L, item: I? = null) {
        unregisterPadlock(pluginLocationToSerialKeyLocation(location), item)
    }

    /**
     * Unregisters a padlock located at the specified location.
     *
     * @param location The location.
     * @param item The item to format.
     */
    private fun unregisterPadlock(location: SerialKeyLocation, item: I?) {
        fixLocation(location)
        super.unregisterPadlock(location)
        if (item != null) {
            if (plugin.itemManager.isUsedKey(item)) {
                plugin.unlocker.setLocation(item, null)
            }
            if (plugin.itemManager.isUsedBunchOfKeys(item)) {
                val toRemove = plugin.itemManager.getBunchOfKeysContent(item).firstOrNull { itemData -> plugin.unlocker.getLocation(itemData) == location }
                if(toRemove != null) {
                    plugin.itemManager.removeDataFromBunchOfKeys(item, toRemove)
                }
            }
        }
    }

    /**
     * Corrects a object (used to handle doors because they are composed from two blocks and double chests too).
     *
     * @param location The object.
     *
     * @return **true :** If the object has been corrected.
     * <br></br>**false :** Otherwise.
     */
    abstract fun fixLocation(location: SerialKeyLocation): Boolean

    /**
     * Converts a plugin location to a SerialKey location.
     *
     * @param location The location.
     *
     * @return The SerialKey location.
     */
    protected abstract fun pluginLocationToSerialKeyLocation(location: L): SerialKeyLocation
}