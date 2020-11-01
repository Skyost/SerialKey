package fr.skyost.serialkey.core.padlock;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.config.SerialKeyData;
import fr.skyost.serialkey.core.object.SerialKeyLocation;

import java.util.Collection;

/**
 * A padlock manager that depends on a plugin.
 *
 * @param <I> ItemStack class.
 * @param <L> Location class.
 */

public abstract class PluginPadlockManager<I, L> extends PadlockManager {

	/**
	 * The plugin instance.
	 */

	private SerialKeyPlugin<I, L> plugin;

	/**
	 * Creates a new plugin padlock manager instance.
	 *
	 * @param plugin The plugin.
	 */

	public PluginPadlockManager(final SerialKeyPlugin<I, L> plugin) {
		this.plugin = plugin;
	}

	/**
	 * Loads the padlocks from the specified data.
	 *
	 * @param data The data.
	 */

	public void load(final SerialKeyData data) {
		final Collection<SerialKeyLocation> locations = data.getPadlocks();
		for(final SerialKeyLocation location : locations) {
			registerPadlock(location);
		}
	}

	/**
	 * Saves the padlocks to the specified data.
	 *
	 * @param data The data.
	 */

	public void save(final SerialKeyData data) {
		for(final SerialKeyLocation location : getPadlocks()) {
			data.addPadlock(location);
		}
	}

	/**
	 * Registers a padlock at the specified location.
	 *
	 * @param location The location.
	 */

	public void registerPadlock(final L location) {
		registerPadlock(location, null);
	}

	/**
	 * Registers a padlock at the specified location.
	 *
	 * @param location The location.
	 * @param item The item to format.
	 */

	public void registerPadlock(final L location, final I item) {
		registerPadlock(pluginLocationToSerialKeyLocation(location), item);
	}

	/**
	 * Registers a padlock at the specified location.
	 *
	 * @param location The location.
	 * @param item The item to format.
	 */

	public void registerPadlock(final SerialKeyLocation location, final I item) {
		super.registerPadlock(location);
		if(item != null && plugin.getItemManager().isBlankKey(item)) {
			plugin.getUnlocker().addLocation(item, location);
		}
	}

	/**
	 * Returns whether or no there is a padlock at the specified location.
	 *
	 * @param location The location.
	 *
	 * @return Whether or no there is a padlock at the specified location.
	 */

	public boolean hasPadlock(final L location) {
		return hasPadlock(pluginLocationToSerialKeyLocation(location));
	}

	@Override
	public boolean hasPadlock(final SerialKeyLocation location) {
		return hasPadlock(location, true);
	}

	/**
	 * Returns whether or no there is a padlock at the specified location.
	 *
	 * @param location The location.
	 * @param fixLocation Whether the location should be fixed.
	 *
	 * @return Whether or no there is a padlock at the specified location.
	 */

	public boolean hasPadlock(final SerialKeyLocation location, final boolean fixLocation) {
		if(fixLocation) {
			fixLocation(location);
		}
		return super.hasPadlock(location);
	}

	/**
	 * Unregisters a padlock located at the specified location.
	 *
	 * @param location The location.
	 */

	private void unregisterPadlock(final L location) {
		unregisterPadlock(location, null);
	}

	@Override
	public void unregisterPadlock(final SerialKeyLocation location) {
		unregisterPadlock(location, null);
	}

	/**
	 * Unregisters a padlock located at the specified location.
	 *
	 * @param location The location.
	 * @param item The item to format.
	 */

	private void unregisterPadlock(final L location, final I item) {
		unregisterPadlock(pluginLocationToSerialKeyLocation(location), item);
	}

	/**
	 * Unregisters a padlock located at the specified location.
	 *
	 * @param location The location.
	 * @param item The item to format.
	 */

	private void unregisterPadlock(final SerialKeyLocation location, final I item) {
		fixLocation(location);
		super.unregisterPadlock(location);
		if(item != null) {
			if(plugin.getItemManager().isUsedKey(item)) {
				plugin.getUnlocker().clearLocations(item);
			}
			if(plugin.getItemManager().isUsedBunchOfKeys(item)) {
				plugin.getUnlocker().removeLocation(item, location);
			}
		}
	}

	/**
	 * Corrects a object (used to handle doors because they are composed from two blocks and double chests too).
	 *
	 * @param location The object.
	 *
	 * @return <b>true :</b> If the object has been corrected.
	 * <br /><b>false :</b> Otherwise.
	 */

	public abstract boolean fixLocation(final SerialKeyLocation location);

	/**
	 * Converts a plugin location to a SerialKey location.
	 *
	 * @param location The location.
	 *
	 * @return The SerialKey location.
	 */

	protected abstract SerialKeyLocation pluginLocationToSerialKeyLocation(final L location);

}