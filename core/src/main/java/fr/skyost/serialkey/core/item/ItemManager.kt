package fr.skyost.serialkey.core.item

import fr.skyost.serialkey.core.util.Util

/**
 * The class that allows to manage plugin items.
 *
 * @param <T> ItemStack class.
 */
abstract class ItemManager<T>
/**
 * Creates a new item manager instance.
 *
 * @param keyItem The key item.
 * @param masterKeyItem The master key item.
 * @param keyCloneItem The key clone item.
 * @param bunchOfKeysItem The bunch of keys item.
 * @param padlockFinderItem The padlock finder item.
 */
protected constructor(var keyItem: T, var masterKeyItem: T, var keyCloneItem: T, var bunchOfKeysItem: T, var padlockFinderItem: T) {

    /**
     * Checks if the specified item is a key (blank or used).
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    abstract fun isKey(item: T?): Boolean

    /**
     * Checks if the specified item is a blank key.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isBlankKey(item: T?): Boolean {
        return isKey(item) && !isUsedKey(item)
    }

    /**
     * Checks if the specified item is an used key.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isUsedKey(item: T?): Boolean {
        return isKey(item) && item != null && getItemData(item).isLoreValid()
    }

    /**
     * Checks if the specified item is a master key.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    abstract fun isMasterKey(item: T?): Boolean

    /**
     * Checks if the specified item is a bunch of keys (blank or used).
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    abstract fun isBunchOfKeys(item: T?): Boolean

    /**
     * Checks if the specified item is a blank bunch of keys.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isBlankBunchOfKeys(item: T?): Boolean {
        return isBunchOfKeys(item) && !isUsedBunchOfKeys(item)
    }

    /**
     * Checks if the specified item is an used bunch of keys.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isUsedBunchOfKeys(item: T?): Boolean {
        if (!isBunchOfKeys(item)) {
            return false
        }
        val data: ItemData = getItemData(item!!)
        return data.isLoreValid() || data.lore?.size == 3
    }

    /**
     * Checks if the specified item is a padlock finder (blank or used).
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    abstract fun isPadlockFinder(item: T?): Boolean

    /**
     * Checks if the specified item is a blank padlock finder.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isBlankPadlockFinder(item: T?): Boolean {
        return isPadlockFinder(item) && !isUsedPadlockFinder(item)
    }

    /**
     * Checks if the specified item is an used padlock finder.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isUsedPadlockFinder(item: T?): Boolean {
        return isPadlockFinder(item) && item != null && getItemData(item).isLoreValid()
    }

    /**
     * Returns the specified item lore (must not be null).
     *
     * @param object The item.
     *
     * @return The specified item data.
     */
    abstract fun getItemData(`object`: T): ItemData

    /**
     * Applies the lore to the specified item.
     *
     * @param object The item.
     * @param data The item data.
     */
    abstract fun setItemData(`object`: T, data: ItemData)

    /**
     * Adds an item to the specified bunch of keys.
     *
     * @param bunchOfKeys The bunch of keys.
     * @param key The key.
     */
    fun addKeyToBunchOfKeys(bunchOfKeys: T, key: T) {
        if (!isBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
            return
        }

        val bunchOfKeysData: ItemData = getItemData(bunchOfKeys)
        val keyData: ItemData = getItemData(key)
        val lore = bunchOfKeysData.lore ?: ArrayList()
        if (lore.isNotEmpty()) {
            lore.add(BUNCH_OF_KEYS_SEPARATOR)
        }
        lore.add(keyData.displayName!!)
        lore.addAll(keyData.lore!!)
        setItemData(bunchOfKeys, bunchOfKeysData)
    }

    /**
     * Returns the specified bunch of keys content.
     *
     * @param bunchOfKeys The bunch of keys.
     *
     * @return The specified bunch of keys content.
     */
    fun getBunchOfKeysContent(bunchOfKeys: T): List<ItemData> {
        val result: ArrayList<ItemData> = ArrayList()
        if (!isUsedBunchOfKeys(bunchOfKeys)) {
            return result
        }

        val lore = getItemData(bunchOfKeys).lore!!
        if (lore.size == 3 || lore.contains(BUNCH_OF_KEYS_SEPARATOR)) {
            var i = 0
            while (i < lore.size) {
                val item: List<String> = lore.dropWhile { element -> element == BUNCH_OF_KEYS_SEPARATOR }
                result.add(ItemData(item[0], ArrayList(item.subList(1, item.size))))
                i += item.size
            }
        } else {
            var i = 0
            while (i < lore.size) {
                result.add(ItemData(null, ArrayList(listOf(lore[i], lore[++i]))))
                i++
            }
        }
        return result;
    }

    /**
     * Removes a key from the specified bunch of key.
     *
     * @param bunchOfKeys The bunch of keys.
     * @param key The key.
     */
    fun removeKeyFromBunchOfKeys(bunchOfKeys: T, key: T) {
        if (!isUsedKey(key)) {
            return
        }

        removeDataFromBunchOfKeys(bunchOfKeys, getItemData(key))
    }

    /**
     * Removes the specified data from the given bunch of key.
     *
     * @param bunchOfKeys The bunch of keys.
     * @param data The data to remove.
     */
    fun removeDataFromBunchOfKeys(bunchOfKeys: T, data: ItemData) {
        if (!isUsedBunchOfKeys(bunchOfKeys)) {
            return
        }

        val bunchOfKeysData: ItemData = getItemData(bunchOfKeys)
        val lore = bunchOfKeysData.lore!!
        val indexOfName: Int = lore.indexOf(data.displayName)
        if (indexOfName > -1) {
            lore.removeAt(indexOfName + 1)
            lore.removeAt(indexOfName)
            if (indexOfName > 0) {
                lore.removeAt(indexOfName - 1)
            }
        } else {
            val keyLore = data.lore
            if (!keyLore.isNullOrEmpty()) {
                val index = Util.sublistIndex(keyLore, lore)
                for (i in index..(index + keyLore.size)) {
                    lore.removeAt(i)
                }
            }
        }
        setItemData(bunchOfKeys, bunchOfKeysData)
    }

    /**
     * Clears all keys from the specified bunch of keys.
     *
     * @param bunchOfKeys The bunch of keys.
     */
    fun clearKeysFromBunchOfKeys(bunchOfKeys: T) {
        if (!isBunchOfKeys(bunchOfKeys)) {
            return
        }

        setItemData(bunchOfKeys, ItemData(getItemData(bunchOfKeys).displayName, null))
    }

    /**
     * Contains various data about an item.
     */
    data class ItemData(val displayName: String?, val lore: MutableList<String>?) {
        fun isLoreValid(): Boolean {
            return lore != null && lore.size % 2 == 0
        }
    }

    companion object {
        /**
         * The separator between two keys in the bunch of keys.
         */
        const val BUNCH_OF_KEYS_SEPARATOR = "---"
    }
}