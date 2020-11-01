package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin

/**
 * A listener that allows to listen anvil related events.
 */
open class AnvilListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when an item has been chosen by a player.
     *
     * @param item The item.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onItemClicked(item: I?, cancelEvent: Runnable) {
        if (item != null && itemManager.isKey(item)) {
            cancelEvent.run()
        }
    }
}