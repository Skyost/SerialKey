package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin

/**
 * A listener that allows to listen bunch of keys related events.
 */
open class BunchOfKeysListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when a player right clicks on air (no block).
     *
     * @param item The item that was used.
     * @param cancelIfCreateInventory Cancels the event if the bunch of keys inventory has been successfully created.
     */
    protected fun onPlayerRightClickOnAir(item: I?, cancelIfCreateInventory: Runnable) {
        if (!itemManager.isBunchOfKeys(item)) {
            return
        }
        cancelIfCreateInventory.run()
    }

    /**
     * Triggered when a player right clicks on a block.
     *
     * @param location The block location.
     * @param item The item that was used.
     * @param cancelIfCreateInventory Cancels the event if the bunch of keys inventory has been successfully created.
     */
    protected fun onPlayerRightClickOnBlock(location: L, item: I?, cancelIfCreateInventory: Runnable) {
        if (padlockManager.hasPadlock(location)) {
            return
        }
        onPlayerRightClickOnAir(item, cancelIfCreateInventory)
    }
}