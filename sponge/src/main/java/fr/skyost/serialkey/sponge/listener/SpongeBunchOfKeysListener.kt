package fr.skyost.serialkey.sponge.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.BunchOfKeysListener
import fr.skyost.serialkey.sponge.item.SpongeItemManager
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * A listener that allows to listen bunch of keys related events.
 */
class SpongeBunchOfKeysListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : BunchOfKeysListener<ItemStack, Location<World>>(plugin) {

    @Listener(order = Order.EARLY)
    fun onPlayerRightClick(event: InteractBlockEvent.Secondary, @First player: Player) {
        val item = player.getItemInHand(event.handType).orElse(null) ?: return
        val block = event.targetBlock
        block.location.ifPresent { location: Location<World> ->
            val cancelIfCreateInventory = Runnable { cancelIfCreateInventory(event, item, player) }
            if (location.blockType === BlockTypes.AIR) {
                super.onPlayerRightClickOnAir(item, cancelIfCreateInventory)
                return@ifPresent
            }
            super.onPlayerRightClickOnBlock(location, item, cancelIfCreateInventory)
        }
    }

    /**
     * Cancels the specified event if the inventory has been successfully created.
     *
     * @param event The event.
     */
    private fun cancelIfCreateInventory(event: Cancellable, item: ItemStack, player: Player) {
        require(itemManager is SpongeItemManager) { "Invalid item manager provided." }
        (itemManager as SpongeItemManager).createInventory(unlocker, item, player)
        event.isCancelled = true
    }
}