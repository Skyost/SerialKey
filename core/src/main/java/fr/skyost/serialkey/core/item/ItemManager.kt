package fr.skyost.serialkey.core.item

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
        return isKey(item) && isLoreValid(item)
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
        return isBunchOfKeys(item) && isLoreValid(item)
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
        return isPadlockFinder(item) && isLoreValid(item)
    }

    /**
     * Returns the specified item lore (must not be null).
     *
     * @param object The item.
     *
     * @return The specified item lore.
     */
    abstract fun getLore(`object`: T?): MutableList<String>

    /**
     * Applies the lore to the specified item.
     *
     * @param object The item.
     * @param lore The lore.
     */
    abstract fun setLore(`object`: T, lore: List<String>?)

    /**
     * Returns whether the lore of the specified item is valid or not.
     *
     * @param object The iteù.
     *
     * @return **true** Yes.
     * <br></br>**false** Otherwise.
     */
    fun isLoreValid(`object`: T?): Boolean {
        val lore = getLore(`object`)
        return lore.isNotEmpty() && lore.size % 2 == 0
    }
}