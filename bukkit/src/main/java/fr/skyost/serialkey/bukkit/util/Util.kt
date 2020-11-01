package fr.skyost.serialkey.bukkit.util

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Contains some useful methods.
 */
object Util {
    /**
     * Picks a random ChatColor.
     *
     * @param exclude If you want to exclude some colors.
     *
     * @return A random ChatColor.
     */
    fun randomChatColor(vararg exclude: ChatColor?): ChatColor {
        val excludeList: List<ChatColor?> = exclude.asList()
        val values = ChatColor.values()
        val random = Random()
        var result: ChatColor
        do {
            result = values[random.nextInt(values.size)]
        } while (excludeList.contains(result))
        return result
    }

    /**
     * Creates an item with a custom name.
     *
     * @param name The name.
     * @param material The item's material.
     *
     * @return The item.
     */
    fun createItem(name: String, material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta!!.setDisplayName(name)
        item.itemMeta = meta
        return item
    }
}