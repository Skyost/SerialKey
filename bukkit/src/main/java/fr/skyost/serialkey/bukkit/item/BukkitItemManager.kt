package fr.skyost.serialkey.bukkit.item

import fr.skyost.serialkey.bukkit.SerialKey
import fr.skyost.serialkey.bukkit.config.BukkitPluginConfig
import fr.skyost.serialkey.bukkit.util.Util
import fr.skyost.serialkey.core.item.PluginItemManager
import fr.skyost.serialkey.core.util.Util.keepAll
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import java.util.function.Function

/**
 * The Bukkit item manager class.
 */
class BukkitItemManager private constructor(private val plugin: SerialKey, config: BukkitPluginConfig, key: ItemStack) : PluginItemManager<ItemStack>(plugin.pluginConfig, key, Util.createItem(config.masterKeyName, config.masterKeyMaterial), key.clone(), Util.createItem(config.bunchOfKeysName, config.bunchOfKeysMaterial), Util.createItem(config.padlockFinderName, Material.COMPASS)) {
    /**
     * Creates a new Bukkit item manager instance.
     *
     * @param plugin The plugin instance.
     */
    constructor(plugin: SerialKey) : this(plugin, plugin.pluginConfig, Util.createItem(plugin.pluginConfig.keyName, plugin.pluginConfig.keyMaterial))

    init {
        keyCloneItem.amount = 2
    }

    override fun <R> createRecipe(id: String, result: ItemStack, shape: List<String>, ingredients: Map<String, String>, register: Function<R, Unit>) {
        var ingredientsMap = ingredients
        val recipe = ShapedRecipe(NamespacedKey(plugin, id), result)
        recipe.shape(*shape.toTypedArray())
        if (ingredientsMap == config.shapeMaterials) {
            ingredientsMap = keepAll(ingredients, shape)
        }
        for ((key, value) in ingredientsMap) {
            recipe.setIngredient(key[0], Material.valueOf(value))
        }
        register.apply(recipe as R)
    }

    override fun isItemValid(item: ItemStack?): Boolean {
        return item != null && (item.itemMeta?.hasDisplayName() ?: false)
    }

    override fun compareItemsName(item1: ItemStack, item2: ItemStack): Boolean {
        return item1.itemMeta!!.displayName == item2.itemMeta!!.displayName
    }

    override fun compareItemsType(item1: ItemStack, item2: ItemStack): Boolean {
        return item1.type == item2.type
    }

    override fun getItemData(`object`: ItemStack): ItemData {
        val meta = `object`.itemMeta
        return ItemData(meta?.displayName ?: "", meta?.lore)
    }

    override fun setItemData(`object`: ItemStack, data: ItemData) {
        val meta = `object`.itemMeta
        if(data.displayName != null) {
            meta?.setDisplayName(data.displayName)
        }
        meta?.lore = data.lore
        `object`.itemMeta = meta
    }

    /**
     * Checks if the specified inventory is a bunch of keys (blank or used).
     *
     * @param event The inventory event.
     *
     * @return **true :** yes.
     * <br></br>**false :** no.
     */
    fun isBunchOfKeys(event: InventoryEvent): Boolean {
        return event.view.title == bunchOfKeysItem.itemMeta!!.displayName && event.inventory.size == 9
    }

    /**
     * Creates an inventory for the specified bunch of keys and opens it for the specified players.
     *
     * @param bunchOfKeys The bunch of keys item.
     * @param players The players.
     *
     * @return The inventory.
     */
    fun createInventory(bunchOfKeys: ItemStack, vararg players: Player): Inventory {
        val inventory = Bukkit.createInventory(null, 9, bunchOfKeys.itemMeta!!.displayName)
        inventory.maxStackSize = 1
        val content: List<ItemData> = getBunchOfKeysContent(bunchOfKeys)

        for (item in content) {
            val key = keyItem.clone()
            val meta = key.itemMeta
            if(item.displayName != null) {
                meta?.setDisplayName(item.displayName)
            }
            meta?.lore = item.lore
            key.itemMeta = meta
            inventory.addItem(key)
        }

        /*final List<SerialKeyLocation> locations = unlocker.getLocations(bunchOfKeys);
		if(!locations.isEmpty()) {
			for(final SerialKeyLocation object : locations) {
				final ItemStack key = getKeyItem().clone();
				unlocker.addLocation(key, object);
				inventory.addItem(key);
			}
		}*/

        for (player in players) {
            player.openInventory(inventory)
        }
        return inventory
    }
}