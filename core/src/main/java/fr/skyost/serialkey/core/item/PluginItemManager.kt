package fr.skyost.serialkey.core.item

import fr.skyost.serialkey.core.config.SerialKeyConfig
import fr.skyost.serialkey.core.util.Util.createMap
import java.util.function.Function

/**
 * An item manager that depends on a plugin.
 *
 * @param <T> ItemStack class.
 */
abstract class PluginItemManager<T>(protected val config: SerialKeyConfig, key: T, masterKey: T, keyClone: T, bunchOfKeys: T, padlockFinder: T) : ItemManager<T>(key, masterKey, keyClone, bunchOfKeys, padlockFinder) {
    /**
     * Creates all required recipes.
     */
    fun <R> createRecipes(register: Function<R, Unit>) {
        createRecipe(KEY_RECIPE_ID, keyItem, config.keyShape, register)
        createRecipe(MASTER_KEY_RECIPE_ID, masterKeyItem, config.masterKeyShape, register)
        createRecipe(KEY_CLONE_RECIPE_ID, keyCloneItem, listOf("YY"), createMap(arrayOf("Y"), arrayOf(config.getKeyMaterialID()))!!, register)
        createRecipe(BUNCH_OF_KEYS_RECIPE_ID, bunchOfKeysItem, config.bunchOfKeysShape, register)
        createRecipe(PADLOCK_FINDER_RECIPE_ID, padlockFinderItem, listOf("ZY"), createMap(arrayOf("Z", "Y"), arrayOf(config.getPadlockFinderMaterialID(), config.getKeyMaterialID()))!!, register)
    }

    /**
     * Creates a recipe for an item.
     *
     * @param id The recipe ID.
     * @param result The item.
     * @param shape The shape.
     */
    private fun <R> createRecipe(id: String, result: T, shape: List<String>, register: Function<R, Unit>) {
        createRecipe(id, result, shape, config.shapeMaterials, register)
    }

    /**
     * Creates a recipe for an item.
     *
     * @param id The recipe ID.
     * @param result The item.
     * @param shape The shape.
     * @param ingredients The ingredients needed for the craft.
     */
    protected abstract fun <R> createRecipe(id: String, result: T, shape: List<String>, ingredients: Map<String, String>, register: Function<R, Unit>)

    override fun isKey(item: T?): Boolean {
        return areItemsEqual(keyItem, item)
    }

    override fun isMasterKey(item: T?): Boolean {
        return areItemsEqual(masterKeyItem, item)
    }

    override fun isBunchOfKeys(item: T?): Boolean {
        return areItemsEqual(bunchOfKeysItem, item)
    }

    override fun isPadlockFinder(item: T?): Boolean {
        return areItemsEqual(padlockFinderItem, item)
    }

    /**
     * Returns whether the specified items are equal.
     *
     * @param item1 The first item.
     * @param item2 The second item.
     *
     * @return **true** Yes.
     * <br></br>**false** Otherwise.
     */
    private fun areItemsEqual(item1: T, item2: T?): Boolean {
        return isItemValid(item2) && compareItemsType(item1, item2!!) && (config.canRenameItems || compareItemsName(item1, item2))
    }

    /**
     * Checks if the specified item is valid.
     *
     * @param item The item.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    protected abstract fun isItemValid(item: T?): Boolean

    /**
     * Compare the specified items name.
     *
     * @param item1 The first item.
     * @param item2 The second item.
     *
     * @return **true :** they are equal.
     * <br></br>**false :** they are not equal.
     */
    protected abstract fun compareItemsName(item1: T, item2: T): Boolean

    /**
     * Compare the specified items type.
     *
     * @param item1 The first item.
     * @param item2 The second item.
     *
     * @return **true :** they are equal.
     * <br></br>**false :** they are not equal.
     */
    protected abstract fun compareItemsType(item1: T, item2: T): Boolean

    companion object {
        /**
         * The key recipe ID.
         */
        const val KEY_RECIPE_ID = "key"

        /**
         * The master key recipe ID.
         */
        const val MASTER_KEY_RECIPE_ID = "master_key"

        /**
         * The key clone recipe ID.
         */
        const val KEY_CLONE_RECIPE_ID = "key_clone"

        /**
         * The bunch of keys recipe ID.
         */
        const val BUNCH_OF_KEYS_RECIPE_ID = "bunch_of_keys"

        /**
         * The padlock finder recipe ID.
         */
        const val PADLOCK_FINDER_RECIPE_ID = "padlock_finder"
    }
}