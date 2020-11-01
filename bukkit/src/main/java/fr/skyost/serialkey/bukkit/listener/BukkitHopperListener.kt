package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.listener.HopperListener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

/**
 * A listener that allows to listen hoppers related events.
 */
class BukkitHopperListener(plugin: SerialKeyPlugin<ItemStack, Location>) : HopperListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced
        if (block.type == Material.HOPPER) {
            super.onBlockPlace(
                    BukkitTypeConverter.toSerialKeyLocation(event.blockPlaced.location),
                    BukkitTypeConverter.toSerialKeyPerson(event.player)
            ) { event.isCancelled = true }
        }
    }

    override fun isChest(location: SerialKeyLocation): Boolean {
        return BukkitTypeConverter.toBukkitLocation(location).block.state is Chest
    }
}