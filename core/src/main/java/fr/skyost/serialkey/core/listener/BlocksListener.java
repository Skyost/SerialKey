package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;

/**
 * A listener that allows to listen blocks related events.
 */

public class BlocksListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Creates a new blocks listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BlocksListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

	/**
	 * Triggered when a block is placed.
	 *
	 * @param item The item that was in player's hand.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected void onBlockPlace(final I item, final Runnable cancelEvent) {
		if(itemManager.isKey(item) || itemManager.isMasterKey(item) || itemManager.isBunchOfKeys(item)/* || itemManager.isPadlockFinder(item)*/) {
			cancelEvent.run();
		}
	}

	/**
	 * Triggered when a block has been broken by a creature.
	 *
	 * @param location The block location.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected boolean onBlockBreakByCreature(final L location, final Runnable cancelEvent) {
		return cancelIfHasPadlock(location, cancelEvent);
	}

	/**
	 * Triggered when a block has been exploded.
	 *
	 * @param location The block location.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected boolean onBlockExplode(final L location, final Runnable cancelEvent) {
		return cancelIfHasPadlock(location, cancelEvent);
	}

	/**
	 * Triggered when a block has been powered by redstone.
	 *
	 * @param location The block location.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected boolean onBlockPoweredByRedstone(final L location, final Runnable cancelEvent) {
		return cancelIfHasPadlock(location, cancelEvent);
	}

	/**
	 * Cancels an event if the specified location has a padlock.
	 *
	 * @param location The block location.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected boolean cancelIfHasPadlock(final L location, final Runnable cancelEvent) {
		if(location != null && padlockManager.hasPadlock(location)) {
			cancelEvent.run();
			return true;
		}

		return false;
	}

}