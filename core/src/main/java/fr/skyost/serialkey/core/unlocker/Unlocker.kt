package fr.skyost.serialkey.core.unlocker

import fr.skyost.serialkey.core.`object`.SerialKeyLocation

/**
 * Represents an unlocker.
 *
 * @param <T> Location class.
 */
interface Unlocker<T> {
    /**
     * Returns the locations located in the item lore.
     *
     * @param item The item.
     *
     * @return All locations located in the item lore.
     */
    fun getLocations(item: T): Collection<SerialKeyLocation?>?

    /**
     * Returns whether the item can unlock the padlock located at the specified location.
     *
     * @param item The item.
     * @param location The location.
     *
     * @return Whether the item can unlock the padlock located at the specified location.
     */
    fun canUnlock(item: T?, location: SerialKeyLocation): Boolean {
        return canUnlock(item, location.world!!, location.position)
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
    fun canUnlock(item: T?, world: String, position: String): Boolean
}