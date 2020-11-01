package fr.skyost.serialkey.core.unlocker

import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.item.ItemManager
import fr.skyost.serialkey.core.util.ROT47
import java.util.*
import java.util.function.Function

/**
 * Represents an unlocker that depends on items lore.
 *
 * @param <T> ItemStack class.
 */
abstract class LoreUnlocker<T>(val itemManager: ItemManager<T>) : Unlocker<T> {
    override fun getLocations(item: T): List<SerialKeyLocation> {
        return getLocations(item, if (isCipheringEnabled) Function { string: String -> ROT47.rotate(string) } else Function { string: String -> string })
    }

    /**
     * Returns the locations located in the item lore.
     *
     * @param item The item.
     * @param stringProcessor The string processor.
     *
     * @return All locations located in the item lore.
     */
    fun getLocations(item: T, stringProcessor: Function<String, String>): List<SerialKeyLocation> {
        val result: MutableList<SerialKeyLocation> = ArrayList<SerialKeyLocation>()
        if (!itemManager.isLoreValid(item)) {
            return result
        }
        val lore = itemManager.getLore(item)
        val n = lore.size
        var i = 0
        while (i < n) {
            result.add(SerialKeyLocation(stringProcessor.apply(lore[i]), stringProcessor.apply(lore[++i])))
            i++
        }
        return result
    }

    override fun canUnlock(item: T?, world: String, position: String): Boolean {
        return canUnlock(item, world, position, if (isCipheringEnabled) Function { string: String -> ROT47.rotate(string) } else Function { string: String -> string })
    }

    /**
     * Returns whether the item can unlock the padlock located at the specified location.
     *
     * @param item The item.
     * @param world The world.
     * @param position The position.
     * @param stringProcessor The string processor.
     *
     * @return Whether the item can unlock the padlock located at the specified location.
     */
    protected fun canUnlock(item: T?, world: String, position: String, stringProcessor: Function<String, String>): Boolean {
        if (itemManager.isMasterKey(item)) {
            return true
        }
        if (!itemManager.isLoreValid(item)) {
            return false
        }
        val lore = itemManager.getLore(item)
        val n = lore.size
        var i = 0
        while (i < n) {
            if (stringProcessor.apply(lore[i]) == world && stringProcessor.apply(lore[++i]) == position) {
                return true
            }
            i++
        }
        return false
    }

    /**
     * Adds a location to the specified collection item.
     *
     * @param collection The collection (bunch of keys for example).
     * @param item The item.
     */
    fun addLocation(collection: T, item: T) {
        if (!itemManager.isLoreValid(item)) {
            return
        }
        val lore = itemManager.getLore(collection)
        lore.addAll(itemManager.getLore(item))
        itemManager.setLore(collection, lore)
    }

    /**
     * Adds a location to the specified item.
     *
     * @param item The item.
     * @param location The location.
     */
    open fun addLocation(item: T, location: SerialKeyLocation) {
        addLocation(item, location.world!!, location.position)
    }

    /**
     * Adds a location to the specified item.
     *
     * @param item The item.
     * @param world The world.
     * @param position The position.
     */
    protected open fun addLocation(item: T, world: String, position: String) {
        addLocation(item, world, position, if (isCipheringEnabled) Function { string: String -> ROT47.rotate(string) } else Function { string: String -> string })
    }

    /**
     * Adds a location to the specified item.
     *
     * @param item The item.
     * @param world The world.
     * @param position The position.
     * @param stringProcessor The string processor.
     */
    protected fun addLocation(item: T, world: String, position: String, stringProcessor: Function<String, String>) {
        val lore = itemManager.getLore(item)
        lore.add(stringProcessor.apply(world))
        lore.add(stringProcessor.apply(position))
        itemManager.setLore(item, lore)
    }

    /**
     * Removes a location from the specified item.
     *
     * @param item The item.
     * @param location The location.
     */
    open fun removeLocation(item: T, location: SerialKeyLocation): Short {
        return removeLocation(item, location.world!!, location.position)
    }

    /**
     * Removes a location from the specified item.
     *
     * @param item The item.
     * @param world The world.
     * @param position The position.
     */
    protected open fun removeLocation(item: T, world: String, position: String): Short {
        return removeLocation(item, world, position, if (isCipheringEnabled) Function { string: String -> ROT47.rotate(string) } else Function { string: String -> string })
    }

    /**
     * Removes a location from the specified item.
     *
     * @param item The item.
     * @param world The world.
     * @param position The position.
     * @param stringProcessor The string processor.
     */
    protected fun removeLocation(item: T, world: String, position: String, stringProcessor: Function<String, String>): Short {
        if (!itemManager.isLoreValid(item)) {
            return 0
        }
        val lore = itemManager.getLore(item)
        var count: Short = 0
        var i = 0
        while (i < lore.size) {
            if (world == stringProcessor.apply(lore[i]) && position == stringProcessor.apply(lore[++i])) {
                lore.removeAt(i)
                lore.removeAt(--i)
                i--
                count++
            }
            i++
        }
        itemManager.setLore(item, if (lore.isEmpty()) null else lore)
        return count
    }

    /**
     * Clears all locations of the specified item.
     *
     * @param item The item.
     */
    fun clearLocations(item: T) {
        itemManager.setLore(item, null)
    }

    /**
     * Returns whether ROT47 ciphering should be enabled.
     *
     * @return Whether ROT47 ciphering should be enabled.
     */
    abstract val isCipheringEnabled: Boolean

}