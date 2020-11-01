package fr.skyost.serialkey.core.padlock

import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import java.util.*

/**
 * The class that allows to manage padlocks.
 */
open class PadlockManager {
    /**
     * The padlocks.
     */
    private val padlocks: HashSet<SerialKeyLocation> = HashSet<SerialKeyLocation>()

    /**
     * Registers a padlock at the specified location.
     *
     * @param location The location.
     */
    fun registerPadlock(location: SerialKeyLocation) {
        padlocks.add(location.copy())
    }

    /**
     * Unregisters a padlock located at the specified location.
     *
     * @param location The location.
     */
    open fun unregisterPadlock(location: SerialKeyLocation) {
        padlocks.remove(location)
    }

    /**
     * Returns whether or no there is a padlock at the specified location.
     *
     * @param location The location.
     *
     * @return Whether or no there is a padlock at the specified location.
     */
    open fun hasPadlock(location: SerialKeyLocation): Boolean {
        return padlocks.contains(location)
    }

    /**
     * Clears all padlocks.
     */
    fun clearPadlocks() {
        padlocks.clear()
    }

    /**
     * Returns all padlocks.
     *
     * @return All padlocks.
     */
    fun getPadlocks(): Set<SerialKeyLocation> {
        return padlocks
    }
}