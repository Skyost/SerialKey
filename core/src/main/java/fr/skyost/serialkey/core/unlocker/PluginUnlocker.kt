package fr.skyost.serialkey.core.unlocker

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import fr.skyost.serialkey.core.util.ROT47.rotate
import java.util.function.Function

/**
 * An unlocker that depends on a plugin.
 *
 * @param <T> ItemStack class.
 */
abstract class PluginUnlocker<T>(private val plugin: SerialKeyPlugin<T, *>) : LoreUnlocker<T>(plugin.itemManager) {
    override fun getLocations(item: T): List<SerialKeyLocation> {
        val stringProcessor: Function<String, String> = if (isCipheringEnabled) Function { string: String -> rotate(stripColor(string)) } else Function { string: String -> stripColor(string) }
        return getLocations(item, stringProcessor)
    }

    override fun addLocation(item: T, location: SerialKeyLocation) {
        plugin.padlockManager.fixLocation(location)
        super.addLocation(item, location)
    }

    public override fun addLocation(item: T, world: String, position: String) {
        val color = randomColor()
        addLocation(item, world, position, if (isCipheringEnabled) Function { string: String -> color + rotate(string) } else Function { string: String -> color + string })
    }

    override fun removeLocation(item: T, location: SerialKeyLocation): Short {
        plugin.padlockManager.fixLocation(location)
        return super.removeLocation(item, location)
    }

    public override fun removeLocation(item: T, world: String, position: String): Short {
        val stringProcessor: Function<String, String> = if (isCipheringEnabled) Function { string: String -> rotate(stripColor(string)) } else Function { string: String -> stripColor(string) }
        return removeLocation(item, world, position, stringProcessor)
    }

    override fun canUnlock(item: T?, location: SerialKeyLocation): Boolean {
        plugin.padlockManager.fixLocation(location)
        return super.canUnlock(item, location)
    }

    override fun canUnlock(item: T?, world: String, position: String): Boolean {
        val stringProcessor: Function<String, String> = if (isCipheringEnabled) Function { string: String -> rotate(stripColor(string)) } else Function { string: String -> stripColor(string) }
        return super.canUnlock(item, world, position, stringProcessor)
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
            return canUnlock(item, location.world!!, location.position)
        }
        if (itemManager.isUsedBunchOfKeys(item)) {
            if (player != null && !player.hasPermission("serialkey.use.bunchofkeys")) {
                plugin.sendMessage(player, plugin.pluginMessages.permissionMessage)
                return false
            }
            return canUnlock(item, location.world!!, location.position)
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
}