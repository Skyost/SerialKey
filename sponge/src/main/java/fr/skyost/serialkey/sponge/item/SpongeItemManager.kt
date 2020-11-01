package fr.skyost.serialkey.sponge.item

import fr.skyost.serialkey.core.item.PluginItemManager
import fr.skyost.serialkey.core.unlocker.LoreUnlocker
import fr.skyost.serialkey.core.util.Util.keepAll
import fr.skyost.serialkey.sponge.BuildConfig
import fr.skyost.serialkey.sponge.SerialKey
import fr.skyost.serialkey.sponge.config.SpongePluginConfig
import fr.skyost.serialkey.sponge.util.Util.createItem
import fr.skyost.serialkey.sponge.util.Util.dropItemAt
import fr.skyost.serialkey.sponge.util.Util.parseString
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryDimension
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * The Sponge item manager class.
 */
class SpongeItemManager private constructor(private val plugin: SerialKey, config: SpongePluginConfig, key: ItemStack) : PluginItemManager<ItemStack>(plugin.pluginConfig, key, createItem(config.masterKeyName, config.masterKeyMaterial), key.copy(), createItem(config.bunchOfKeysName, config.bunchOfKeysMaterial), createItem(config.padlockFinderName, ItemTypes.COMPASS)) {
    /**
     * Creates a new Sponge item manager instance.
     *
     * @param plugin The plugin instance.
     */
    constructor(plugin: SerialKey) : this(plugin, plugin.pluginConfig, createItem(plugin.pluginConfig.keyName, plugin.pluginConfig.keyMaterial))

    /**
     * Creates a recipe for an item.
     *
     * @param id The recipe ID.
     * @param result The item.
     * @param shape The shape.
     * @param ingredients The ingredients needed for the craft.
     */
    override fun <R> createRecipe(id: String, result: ItemStack, shape: List<String>, ingredients: Map<String, String>, register: Function<R, Unit>) {
        var recipeIngredients = ingredients
        if (recipeIngredients == plugin.pluginConfig.shapeMaterials) {
            recipeIngredients = keepAll(ingredients, shape)
        }
        val registry = Sponge.getRegistry()
        val where: MutableMap<Char, Ingredient> = HashMap()
        recipeIngredients.forEach { (character: String, type: String?) -> registry.getType(ItemType::class.java, type).ifPresent { itemType: ItemType? -> where[character[0]] = Ingredient.of(itemType) } }
        val recipe: ShapedCraftingRecipe = ShapedCraftingRecipe.builder()
                .aisle(*shape.toTypedArray())
                .where(where)
                .result(result)
                .id(id)
                .build()
        register.apply(recipe as R)
    }

    override fun isItemValid(item: ItemStack?): Boolean {
        return item != null && item.get(Keys.DISPLAY_NAME).isPresent
    }

    override fun compareItemsName(item1: ItemStack, item2: ItemStack): Boolean {
        return item1.get(Keys.DISPLAY_NAME) == item2.get(Keys.DISPLAY_NAME)
    }

    override fun compareItemsType(item1: ItemStack, item2: ItemStack): Boolean {
        return item1.type === item2.type
    }

    override fun getItemData(`object`: ItemStack): ItemData {
        val rawLore = `object`.get(Keys.ITEM_LORE)
        val lore = rawLore.map { texts: List<Text> -> texts.stream().map { text: Text -> TextSerializers.FORMATTING_CODE.serialize(text) }.collect(Collectors.toList()) }.get()
        return ItemData(TextSerializers.FORMATTING_CODE.serialize(`object`.get(Keys.DISPLAY_NAME).get()), lore)
    }

    override fun setItemData(`object`: ItemStack, data: ItemData) {
        if(data.displayName != null) {
            `object`.offer(Keys.DISPLAY_NAME, parseString(data.displayName!!))
        }
        `object`.offer(Keys.ITEM_LORE, if (data.lore == null) java.util.ArrayList() else data.lore!!.stream().map { string: String -> parseString(string) }.collect(Collectors.toList()))
    }

    /**
     * Creates an inventory for the specified bunch of keys and opens it for the specified players.
     *
     * @param unlocker The unlocker.
     * @param bunchOfKeys The bunch of keys item.
     * @param players The players.
     *
     * @return The inventory.
     */
    fun createInventory(unlocker: LoreUnlocker<ItemStack>, bunchOfKeys: ItemStack, vararg players: Player): Inventory {
        val inventory = Inventory.builder()
                .property(InventoryTitle.of(bunchOfKeys.get(Keys.DISPLAY_NAME).orElse(parseString(BuildConfig.PLUGIN_NAME))))
                .property(InventoryDimension.of(9, 1))
                .listener(InteractInventoryEvent.Close::class.java) { event: InteractInventoryEvent.Close -> event.cause.first(Player::class.java).ifPresent { player: Player -> onBunchOfKeysInventoryClose(unlocker, event.targetInventory.first(), bunchOfKeys, player) } }
                .build(plugin)
        val lore = bunchOfKeys.get(Keys.ITEM_LORE).orElse(java.util.ArrayList())
        val n = lore.size
        var i = 0
        while (i < n) {
            val key = keyItem.copy()
            key.offer(Keys.ITEM_LORE, listOf(lore[i], lore[++i]))
            inventory.offer(key)
            i++
        }
        for (player in players) {
            player.openInventory(inventory)
        }
        return inventory
    }

    /**
     * Triggered when a bunch of keys inventory is closed.
     *
     * @param unlocker The unlocker.
     * @param inventory The inventory.
     * @param bunchOfKeys The bunch of keys item.
     * @param player The involved player.
     */
    private fun onBunchOfKeysInventoryClose(unlocker: LoreUnlocker<ItemStack>, inventory: Inventory, bunchOfKeys: ItemStack, player: Player) {
        if (bunchOfKeys.quantity > 1) {
            val clone = bunchOfKeys.copy()
            clone.quantity = bunchOfKeys.quantity - 1
            dropItemAt(clone, player.location)
            bunchOfKeys.quantity = 1
        }
        unlocker.clearLocations(bunchOfKeys)
        while (inventory.size() > 0) {
            inventory.poll().ifPresent { item: ItemStack ->
                if (!isUsedKey(item)) {
                    dropItemAt(item, player.location)
                    return@ifPresent
                }
                addKeyToBunchOfKeys(bunchOfKeys, item)
                if (item.quantity > 1) {
                    val clone = item.copy()
                    clone.quantity = item.quantity - 1
                    dropItemAt(clone, player.location)
                }
            }
        }
    }

    init {
        keyCloneItem.quantity = 2
    }
}