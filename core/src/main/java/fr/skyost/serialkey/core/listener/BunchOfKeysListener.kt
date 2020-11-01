package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;

/**
 * A listener that allows to listen bunch of keys related events.
 */

public class BunchOfKeysListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Creates a new bunch of keys listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BunchOfKeysListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

	/**
	 * Triggered when a player right clicks on air (no block).
	 *
	 * @param item The item that was used.
	 * @param cancelIfCreateInventory Cancels the event if the bunch of keys inventory has been successfully created.
	 */

	protected void onPlayerRightClickOnAir(final I item, final Runnable cancelIfCreateInventory) {
		if(!itemManager.isBunchOfKeys(item)) {
			return;
		}

		cancelIfCreateInventory.run();
	}

	/**
	 * Triggered when a player right clicks on a block.
	 *
	 * @param location The block location.
	 * @param item The item that was used.
	 * @param cancelIfCreateInventory Cancels the event if the bunch of keys inventory has been successfully created.
	 */

	protected void onPlayerRightClickOnBlock(final L location, final I item, final Runnable cancelIfCreateInventory) {
		if(padlockManager.hasPadlock(location)) {
			return;
		}

		onPlayerRightClickOnAir(item, cancelIfCreateInventory);
	}

}