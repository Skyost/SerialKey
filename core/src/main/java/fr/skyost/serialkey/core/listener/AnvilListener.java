package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;

/**
 * A listener that allows to listen anvil related events.
 */

public class AnvilListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Creates a new anvil listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public AnvilListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

	/**
	 * Triggered when an item has been chosen by a player.
	 *
	 * @param item The item.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected void onItemClicked(final I item, final Runnable cancelEvent) {
		if(item != null && itemManager.isKey(item)) {
			cancelEvent.run();
		}
	}

}