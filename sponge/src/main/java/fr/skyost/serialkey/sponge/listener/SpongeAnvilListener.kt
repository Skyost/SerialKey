package fr.skyost.serialkey.sponge.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.AnvilListener
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * A listener that allows to listen anvil related events.
 */
class SpongeAnvilListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : AnvilListener<ItemStack, Location<World>>(plugin) {
    @Listener(order = Order.EARLY)
    fun onInventoryClick(event: ClickInventoryEvent, @First player: Player?) {
        if (event.targetInventory.archetype !== InventoryArchetypes.ANVIL) {
            return
        }
        super.onItemClicked(event.cursorTransaction.final.createStack()) { event.isCancelled = true }
    }
}