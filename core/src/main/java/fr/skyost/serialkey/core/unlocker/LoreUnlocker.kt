package fr.skyost.serialkey.core.unlocker

import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.item.ItemManager
import fr.skyost.serialkey.core.util.ROT47

/**
 * Represents an unlocker that depends on items lore.
 *
 * @param <T> ItemStack class.
 */
abstract class LoreUnlocker<T>(val itemManager: ItemManager<T>) : Unlocker<T> {
    /**
     * Returns the locations located in the item lore.
     *
     * @param item The item.
     *
     * @return All locations located in the item lore.
     */
    override fun getLocations(item: T?): List<SerialKeyLocation> {
        val result: MutableList<SerialKeyLocation> = ArrayList()
        if (item == null || !itemManager.getItemData(item).isLoreValid()) {
            return result
        }

        val data: List<ItemManager.ItemData> = if (itemManager.isUsedBunchOfKeys(item)) itemManager.getBunchOfKeysContent(item) else listOf(itemManager.getItemData(item))
        return data.mapNotNull { itemData -> getLocation(itemData) }
    }

    /**
     * Returns the location contained in the specified individual item data.
     *
     * @param data The item data.
     *
     * @return The location contained in the specified individual item data.
     */

    fun getLocation(data: ItemManager.ItemData): SerialKeyLocation? {
        if (!data.isLoreValid()) {
            return null
        }
        return SerialKeyLocation(processString(data.lore!![0], false), processString(data.lore[1], false))
    }

    /**
     * Returns whether the item can unlock the padlock located at the specified location.
     *
     * @param item The item.
     * @param location The location.
     *
     * @return Whether the item can unlock the padlock located at the specified location.
     */
    override fun canUnlock(item: T?, location: SerialKeyLocation): Boolean {
        if (itemManager.isMasterKey(item)) {
            return true
        }

        return getLocations(item).contains(location)
    }

    /**
     * Sets the location of the specified item.
     *
     * @param item The item.
     * @param location The location.
     */
    protected open fun setLocation(item: T, location: SerialKeyLocation?) {
        val data = itemManager.getItemData(item)
        val lore: ArrayList<String>? = if (location == null) null else ArrayList(listOf(processString(location.world!!, true), processString(location.position, true)))
        itemManager.setItemData(item, ItemManager.ItemData(data.displayName, lore))
    }

    /**
     * Clears all locations of the specified item.
     *
     * @param item The item.
     */
    fun clearLocations(item: T) {
        itemManager.setItemData(item, ItemManager.ItemData(itemManager.getItemData(item).displayName, null))
    }

    /**
     * Returns whether ROT47 ciphering should be enabled.
     *
     * @return Whether ROT47 ciphering should be enabled.
     */
    abstract val isCipheringEnabled: Boolean

    /**
     * Process a given string.
     *
     * @param input The input string.
     * @param write Whether this is a writing operation.
     */

    open fun processString(input: String, write: Boolean): String {
        return if (isCipheringEnabled) ROT47.rotate(input) else input
    }
}