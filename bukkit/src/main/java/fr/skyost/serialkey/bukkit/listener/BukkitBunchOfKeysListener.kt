package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.bukkit.item.BukkitItemManager
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.BunchOfKeysListener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * A listener that allows to listen bunch of keys related events.
 */
class BukkitBunchOfKeysListener(plugin: SerialKeyPlugin<ItemStack, Location>) : BunchOfKeysListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        when (event.action) {
            Action.RIGHT_CLICK_BLOCK -> super.onPlayerRightClickOnBlock(event.clickedBlock!!.location, event.item) { cancelIfCreateInventory(event) }
            Action.RIGHT_CLICK_AIR -> super.onPlayerRightClickOnAir(event.item) { cancelIfCreateInventory(event) }
            else -> {}
        }
    }

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        require(itemManager is BukkitItemManager) { "Invalid item manager provided." }
        if (!(itemManager as BukkitItemManager).isBunchOfKeys(event)) {
            return
        }
        val item = event.currentItem
        if (item!!.type != Material.AIR && !itemManager.isUsedKey(item)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        require(itemManager is BukkitItemManager) { "Invalid item manager provided." }
        if (!(itemManager as BukkitItemManager).isBunchOfKeys(event)) {
            return
        }
        val player = event.player
        var bunchOfKeys = player.inventory.itemInOffHand
        if (!itemManager.isBunchOfKeys(bunchOfKeys)) {
            bunchOfKeys = player.inventory.itemInMainHand
        }
        if (bunchOfKeys.amount > 1) {
            val clone = bunchOfKeys.clone()
            clone.amount = bunchOfKeys.amount - 1
            player.world.dropItemNaturally(player.eyeLocation, clone)
            bunchOfKeys.amount = 1
        }
        unlocker.clearLocations(bunchOfKeys)
        val inventory = event.inventory
        val n = inventory.size
        for (i in 0 until n) {
            val item = inventory.getItem(i) ?: continue
            if (!itemManager.isUsedKey(item)) {
                player.world.dropItemNaturally(player.location, item)
                continue
            }
            unlocker.addLocation(bunchOfKeys, item)
            if (item.amount > 1) {
                val clone = item.clone()
                clone.amount = item.amount - 1
                player.world.dropItemNaturally(player.eyeLocation, clone)
            }
        }
    }

    /**
     * Cancels the specified event if the inventory has been successfully created.
     *
     * @param event The event.
     */
    private fun cancelIfCreateInventory(event: PlayerInteractEvent) {
        require(itemManager is BukkitItemManager) { "Invalid item manager provided." }
        (itemManager as BukkitItemManager).createInventory(event.item!!, event.player)
        event.isCancelled = true
    }
}