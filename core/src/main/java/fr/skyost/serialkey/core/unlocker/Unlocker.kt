package fr.skyost.serialkey.core.unlocker;

import fr.skyost.serialkey.core.object.SerialKeyLocation;

import java.util.Collection;

/**
 * Represents an unlocker.
 *
 * @param <T> Location class.
 */

public interface Unlocker<T> {

	/**
	 * Returns the locations located in the item lore.
	 *
	 * @param item The item.
	 *
	 * @return All locations located in the item lore.
	 */

	Collection<SerialKeyLocation> getLocations(final T item);

	/**
	 * Returns whether the item can unlock the padlock located at the specified location.
	 *
	 * @param item The item.
	 * @param location The location.
	 *
	 * @return Whether the item can unlock the padlock located at the specified location.
	 */

	default boolean canUnlock(final T item, final SerialKeyLocation location) {
		return canUnlock(item, location.getWorld(), location.getPosition());
	}

	/**
	 * Returns whether the item can unlock the padlock located at the specified location.
	 *
	 * @param item The item.
	 * @param world The world.
	 * @param position The position.
	 *
	 * @return Whether the item can unlock the padlock located at the specified location.
	 */

	boolean canUnlock(final T item, final String world, final String position);

}