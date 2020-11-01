package fr.skyost.serialkey.core.unlocker

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import fr.skyost.serialkey.core.util.ROT47.rotate

/**
 * An unlocker that depends on a plugin.
 *
 * @param <T> ItemStack class.
 */
abstract class PluginUnlocker<T>(private val plugin: SerialKeyPlugin<T, *>) : LoreUnlocker<T>(plugin.itemManager) {
    override fun canUnlock(item: T?, location: SerialKeyLocation): Boolean {
        plugin.padlockManager.fixLocation(location)
        return super.canUnlock(item, location)
    }

    public override fun setLocation(item: T, location: SerialKeyLocation?) {
        if (location != null) {
            plugin.padlockManager.fixLocation(location)
        }
        super.setLocation(item, location)
    }

    override val isCipheringEnabled: Boolean
        get() = plugin.pluginConfig.encryptLore

    /**
     * Checks if the specified item is a valid key for the specified object.
     *
     * @param item The object.
     * @param location The location.
     * @param player If you want to check if the player has the right permission. Will send a message if he doesn't.
     *
     * @return **true** : yes.
     * <br></br>**false** : no.
     */
    fun canUnlock(item: T?, location: SerialKeyLocation, player: SerialKeyPerson?): Boolean {
        if (itemManager.isMasterKey(item)) {
            if (player != null && !player.hasPermission("serialkey.use.masterkey")) {
                plugin.sendMessage(player, plugin.pluginMessages.permissionMessage)
            }
            return true
        }
        plugin.padlockManager.fixLocation(location)
        if (itemManager.isUsedKey(item)) {
            if (player != null && !player.hasPermission("serialkey.use.key")) {
                plugin.sendMessage(player, plugin.pluginMessages.permissionMessage)
                return false
            }
            return canUnlock(item, location)
        }
        if (itemManager.isUsedBunchOfKeys(item)) {
            if (player != null && !player.hasPermission("serialkey.use.bunchofkeys")) {
                plugin.sendMessage(player, plugin.pluginMessages.permissionMessage)
                return false
            }
            return canUnlock(item, location)
        }
        return false
    }

    /**
     * Returns a random chat color.
     *
     * @return A random chat color.
     */
    protected abstract fun randomColor(): String

    /**
     * Strips all colors from the specified string.
     *
     * @param string The string.
     *
     * @return The handled string.
     */
    protected abstract fun stripColor(string: String): String

    override fun processString(input: String, write: Boolean): String {
        return if (write) randomColor() + rotate(input) else rotate(stripColor(input))
    }
}