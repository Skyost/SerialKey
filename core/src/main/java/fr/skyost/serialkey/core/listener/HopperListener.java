package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;

/**
 * A listener that allows to listen hoppers related events.
 */

public abstract class HopperListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Directions to check for chests.
	 */

	private static final SerialKeyLocation[] DIRECTIONS = new SerialKeyLocation[]{
			new SerialKeyLocation(null, 0, 1, 0),
			new SerialKeyLocation(null, 0, -1, 0),
			new SerialKeyLocation(null, 0, 0, -1),
			new SerialKeyLocation(null, 1, 0, 0),
			new SerialKeyLocation(null, 0, 0, 1),
			new SerialKeyLocation(null, -1, 0, 0)
	};

	/**
	 * Creates a new hoppers listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public HopperListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

	/**
	 * Triggered when a block has been placed.
	 *
	 * @param location The location.
	 * @param player The involved player.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected void onBlockPlace(final SerialKeyLocation location, final SerialKeyPerson player, final Runnable cancelEvent) {
		for(final SerialKeyLocation direction : DIRECTIONS) {
			final SerialKeyLocation relative = location.getRelative(direction);
			if(!(isChest(relative)) || !padlockManager.hasPadlock(relative)) {
				continue;
			}

			plugin.sendMessage(player, plugin.getPluginMessages().getBlockHasPadlockMessage());
			cancelEvent.run();
			return;
		}
	}

	/**
	 * Returns whether a chest is located at the specified location.
	 *
	 * @param location The location.
	 *
	 * @return Whether a chest is located at the specified location.
	 */

	protected abstract boolean isChest(final SerialKeyLocation location);

}