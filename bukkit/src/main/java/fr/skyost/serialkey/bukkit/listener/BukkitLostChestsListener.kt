package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.LostChestsListener
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.block.DoubleChest
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * A listener that allows to listen chests related events.
 */
class BukkitLostChestsListener(plugin: SerialKeyPlugin<ItemStack, Location>) : LostChestsListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        var location: Location? = null
        if (holder is Chest) {
            location = holder.location
        } else if (holder is DoubleChest) {
            location = holder.location
        }
        if (location == null) {
            return
        }
        super.onInventoryClick(
                BukkitTypeConverter.toSerialKeyPerson(event.whoClicked),
                BukkitTypeConverter.toSerialKeyLocation(location),
                event.currentItem
        ) { event.isCancelled = true }
    }
}