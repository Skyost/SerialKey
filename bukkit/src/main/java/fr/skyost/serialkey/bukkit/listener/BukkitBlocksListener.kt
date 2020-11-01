package fr.skyost.serialkey.bukkit.listener

import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.bukkit.util.DoorUtil
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.BlocksListener
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityBreakDoorEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * A listener that allows to listen blocks related events.
 */
class BukkitBlocksListener(plugin: SerialKeyPlugin<ItemStack, Location>) : BlocksListener<ItemStack, Location>(plugin), Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onBlockPlace(event: BlockPlaceEvent) {
        super.onBlockPlace(event.itemInHand) { event.isCancelled = true }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onEntityBreakDoor(event: EntityBreakDoorEvent) {
        super.onBlockBreakByCreature(event.block.location) { event.isCancelled = true }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onEntityExplode(event: EntityExplodeEvent) {
        val blocks = event.blockList()
        for (block in ArrayList(blocks)) {
            super.onBlockExplode(block.location) { blocks.remove(block) }
            protectUpperDoor(block) { blocks.remove(block) }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onBlockRedstone(event: BlockRedstoneEvent) {
        super.onBlockPoweredByRedstone(event.block.location) { event.newCurrent = 0 }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onBlockBreak(event: BlockBreakEvent) {
        if (protectUpperDoor(event)) {
            plugin.sendMessage(BukkitTypeConverter.toSerialKeyPerson(event.player), plugin.pluginMessages.blockHasPadlockMessage)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onBlockIgnite(event: BlockIgniteEvent) {
        protectUpperDoor(event)
    }

    /**
     * Protects the door that is up to the specified block.
     *
     * @param event The block event.
     * @param <T> The event type.
     *
     * @return Whether the event has been cancelled or not.
    */
    private fun <T> protectUpperDoor(event: T): Boolean where T : BlockEvent?, T : Cancellable? {
        return protectUpperDoor(event!!.block) { event.isCancelled = true }
    }

    /**
     * Protects the door that is up to the specified block.
     *
     * @param event The block event.
     * @param cancelEvent The runnable that allows to cancel the event.
     *
     * @return Whether the event has been cancelled or not.
     */
    private fun protectUpperDoor(event: Block, cancelEvent: Runnable): Boolean {
        val up = event.getRelative(BlockFace.UP)
        return if (DoorUtil.getInstance(up.blockData) == null) {
            false
        } else super.cancelIfHasPadlock(up.location, cancelEvent)
    }
}