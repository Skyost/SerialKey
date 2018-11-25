package fr.skyost.serialkey.core.padlock;

import fr.skyost.serialkey.core.object.SerialKeyLocation;

import java.util.HashSet;
import java.util.Set;

/**
 * The class that allows to manage padlocks.
 */

public class PadlockManager {

	/**
	 * The padlocks.
	 */

	private final HashSet<SerialKeyLocation> padlocks = new HashSet<>();

	/**
	 * Registers a padlock at the specified location.
	 *
	 * @param location The location.
	 */

	public void registerPadlock(final SerialKeyLocation location) {
		padlocks.add(location.copy());
	}

	/**
	 * Unregisters a padlock located at the specified location.
	 *
	 * @param location The location.
	 */

	public void unregisterPadlock(final SerialKeyLocation location) {
		padlocks.remove(location);
	}

	/**
	 * Returns whether or no there is a padlock at the specified location.
	 *
	 * @param location The location.
	 *
	 * @return Whether or no there is a padlock at the specified location.
	 */

	public boolean hasPadlock(final SerialKeyLocation location) {
		return padlocks.contains(location);
	}

	/**
	 * Clears all padlocks.
	 */

	public void clearPadlocks() {
		padlocks.clear();
	}

	/**
	 * Returns all padlocks.
	 *
	 * @return All padlocks.
	 */

	public Set<SerialKeyLocation> getPadlocks() {
		return padlocks;
	}

}