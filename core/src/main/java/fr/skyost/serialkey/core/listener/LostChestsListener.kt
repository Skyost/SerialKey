package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson

/**
 * A listener that allows to listen chests related events.
 */
open class LostChestsListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when a click occurs in an inventory.
     *
     * @param player The involved player.
     * @param chestLocation The chest location.
     * @param item The clicked item.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onInventoryClick(player: SerialKeyPerson, chestLocation: SerialKeyLocation, item: I?, cancelEvent: Runnable) {
        if (!itemManager.isUsedKey(item) && !itemManager.isUsedBunchOfKeys(item)) {
            return
        }
        padlockManager.fixLocation(chestLocation)
        if (!padlockManager.hasPadlock(chestLocation, false)) {
            return
        }
        for (location in unlocker.getLocations(item!!)) {
            padlockManager.fixLocation(location)
            if (location.equals(chestLocation)) {
                cancelEvent.run()
                plugin.sendMessage(player, plugin.pluginMessages.chestProtectionMessage)
                return
            }
        }
    }
}