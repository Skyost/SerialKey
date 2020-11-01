package fr.skyost.serialkey.bukkit.unlocker

import fr.skyost.serialkey.bukkit.SerialKey
import fr.skyost.serialkey.bukkit.util.Util
import fr.skyost.serialkey.core.unlocker.PluginUnlocker
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

/**
 * The Bukkit unlocker class.
 */
class BukkitUnlocker(plugin: SerialKey) : PluginUnlocker<ItemStack>(plugin) {
    override fun randomColor(): String {
        return Util.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC, ChatColor.BLACK).toString()
    }

    override fun stripColor(string: String): String {
        return ChatColor.stripColor(string)!!
    }
}