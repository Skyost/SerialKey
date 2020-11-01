package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import java.util.function.Consumer

/**
 * A listener that allows to listen padlock finder related events.
 */
open class PadlockFinderListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when a player right clicks.
     *
     * @param item The item.
     * @param player The involved player.
     * @param spawn The spawn location.
     * @param currentCompassesTarget The current compasses target.
     * @param setCompassesTarget Function that allows to change the current compasses target.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onPlayerRightClick(item: I?, player: SerialKeyPerson, spawn: SerialKeyLocation, currentCompassesTarget: SerialKeyLocation, setCompassesTarget: Consumer<SerialKeyLocation>, cancelEvent: Runnable) {
        if (!itemManager.isUsedPadlockFinder(item)) {
            return
        }
        val padlockLocation: SerialKeyLocation = unlocker.getLocations(item!!)[0]
        if (currentCompassesTarget.equals(spawn)) {
            setCompassesTarget.accept(padlockLocation)
            plugin.sendMessage(player, plugin.pluginMessages.padlockFinderEnabledMessage)
        } else {
            setCompassesTarget.accept(spawn)
            plugin.sendMessage(player, plugin.pluginMessages.padlockFinderDisabledMessage)
        }
        cancelEvent.run()
    }
}