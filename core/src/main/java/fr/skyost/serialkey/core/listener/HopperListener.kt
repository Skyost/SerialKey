package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson

/**
 * A listener that allows to listen hoppers related events.
 */
abstract class HopperListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when a block has been placed.
     *
     * @param location The location.
     * @param player The involved player.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onBlockPlace(location: SerialKeyLocation, player: SerialKeyPerson, cancelEvent: Runnable) {
        for (direction in DIRECTIONS) {
            val relative: SerialKeyLocation = location.getRelative(direction)
            if (!isChest(relative) || !padlockManager.hasPadlock(relative)) {
                continue
            }
            plugin.sendMessage(player, plugin.pluginMessages.blockHasPadlockMessage)
            cancelEvent.run()
            return
        }
    }

    /**
     * Returns whether a chest is located at the specified location.
     *
     * @param location The location.
     *
     * @return Whether a chest is located at the specified location.
     */
    protected abstract fun isChest(location: SerialKeyLocation): Boolean

    companion object {
        /**
         * Directions to check for chests.
         */
        private val DIRECTIONS: Array<SerialKeyLocation> = arrayOf(
                SerialKeyLocation(null, 0, 1, 0),
                SerialKeyLocation(null, 0, -1, 0),
                SerialKeyLocation(null, 0, 0, -1),
                SerialKeyLocation(null, 1, 0, 0),
                SerialKeyLocation(null, 0, 0, 1),
                SerialKeyLocation(null, -1, 0, 0)
        )
    }
}