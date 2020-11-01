package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.AnvilListener
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

/**
 * A listener that allows to listen anvil related events.
 */
class BukkitAnvilListener(plugin: SerialKeyPlugin<ItemStack, Location>) : AnvilListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun onInventoryClick(event: InventoryClickEvent) {
        if (event.inventory.type != InventoryType.ANVIL) {
            return
        }
        super.onItemClicked(event.currentItem) { event.isCancelled = true }
    }
}