package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.listener.PadlockFinderListener
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * A listener that allows to listen padlock finder related events.
 */
class BukkitPadlockFinderListener(plugin: SerialKeyPlugin<ItemStack, Location>) : PadlockFinderListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        val player = event.player
        super.onPlayerRightClick(
                event.item,
                BukkitTypeConverter.toSerialKeyPerson(player),
                BukkitTypeConverter.toSerialKeyLocation(player.world.spawnLocation),
                BukkitTypeConverter.toSerialKeyLocation(player.compassTarget),
                { location: SerialKeyLocation -> player.compassTarget = BukkitTypeConverter.toBukkitLocation(location) },
                { event.isCancelled = true }
        )
    }
}