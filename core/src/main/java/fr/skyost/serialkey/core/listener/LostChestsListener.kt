package fr.skyost.serialkey.core.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;

/**
 * A listener that allows to listen chests related events.
 */

public class LostChestsListener<I, L> extends SerialKeyListener<I, L> {

	/**
	 * Creates a new lost chests listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public LostChestsListener(final SerialKeyPlugin<I, L> plugin) {
		super(plugin);
	}

	/**
	 * Triggered when a click occurs in an inventory.
	 *
	 * @param player The involved player.
	 * @param chestLocation The chest location.
	 * @param item The clicked item.
	 * @param cancelEvent The runnable that cancels the event.
	 */

	protected void onInventoryClick(final SerialKeyPerson player, final SerialKeyLocation chestLocation, final I item, final Runnable cancelEvent) {
		if(!itemManager.isUsedKey(item) && !itemManager.isUsedBunchOfKeys(item)) {
			return;
		}


		padlockManager.fixLocation(chestLocation);
		if(!padlockManager.hasPadlock(chestLocation, false)) {
			return;
		}

		for(final SerialKeyLocation location : unlocker.getLocations(item)) {
			padlockManager.fixLocation(location);
			if(location.equals(chestLocation)) {
				cancelEvent.run();
				plugin.sendMessage(player, plugin.getPluginMessages().getChestProtectionMessage());
				return;
			}
		}
	}

}