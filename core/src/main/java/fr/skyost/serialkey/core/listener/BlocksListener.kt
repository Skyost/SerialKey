package fr.skyost.serialkey.core.listener

import fr.skyost.serialkey.core.SerialKeyPlugin

/**
 * A listener that allows to listen blocks related events.
 */
open class BlocksListener<I, L>(plugin: SerialKeyPlugin<I, L>) : SerialKeyListener<I, L>(plugin) {
    /**
     * Triggered when a block is placed.
     *
     * @param item The item that was in player's hand.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onBlockPlace(item: I, cancelEvent: Runnable) {
        if (itemManager.isKey(item) || itemManager.isMasterKey(item) || itemManager.isBunchOfKeys(item) /* || itemManager.isPadlockFinder(item)*/) {
            cancelEvent.run()
        }
    }

    /**
     * Triggered when a block has been broken by a creature.
     *
     * @param location The block location.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onBlockBreakByCreature(location: L, cancelEvent: Runnable): Boolean {
        return cancelIfHasPadlock(location, cancelEvent)
    }

    /**
     * Triggered when a block has been exploded.
     *
     * @param location The block location.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onBlockExplode(location: L, cancelEvent: Runnable): Boolean {
        return cancelIfHasPadlock(location, cancelEvent)
    }

    /**
     * Triggered when a block has been powered by redstone.
     *
     * @param location The block location.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun onBlockPoweredByRedstone(location: L, cancelEvent: Runnable): Boolean {
        return cancelIfHasPadlock(location, cancelEvent)
    }

    /**
     * Cancels an event if the specified location has a padlock.
     *
     * @param location The block location.
     * @param cancelEvent The runnable that cancels the event.
     */
    protected fun cancelIfHasPadlock(location: L?, cancelEvent: Runnable): Boolean {
        if (location != null && padlockManager.hasPadlock(location)) {
            cancelEvent.run()
            return true
        }
        return false
    }
}