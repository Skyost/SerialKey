package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;

import java.util.function.Consumer;

/**
 * A listener that allows to listen padlock finder related events.
 */

public class PadlockFinderListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Creates a new padlock finder listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public PadlockFinderListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

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

	protected void onPlayerRightClick(final I item, final SerialKeyPerson player, final SerialKeyLocation spawn, final SerialKeyLocation currentCompassesTarget, final Consumer<SerialKeyLocation> setCompassesTarget, final Runnable cancelEvent) {
		if(!itemManager.isUsedPadlockFinder(item)) {
			return;
		}

		final SerialKeyLocation padlockLocation = unlocker.getLocations(item).get(0);
		if(currentCompassesTarget.equals(spawn)) {
			setCompassesTarget.accept(padlockLocation);
			plugin.sendMessage(player, plugin.getPluginMessages().getPadlockFinderEnabledMessage());
		}
		else {
			setCompassesTarget.accept(spawn);
			plugin.sendMessage(player, plugin.getPluginMessages().getPadlockFinderDisabledMessage());
		}

		cancelEvent.run();
	}

}