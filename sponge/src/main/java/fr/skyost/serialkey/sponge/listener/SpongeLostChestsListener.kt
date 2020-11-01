package fr.skyost.serialkey.sponge.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.LostChestsListener
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyLocation
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyPerson
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.BlockCarrier
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.type.CarriedInventory
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * A listener that allows to listen chests related events.
 */
class SpongeLostChestsListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : LostChestsListener<ItemStack, Location<World>>(plugin) {
    @Listener(order = Order.EARLY)
    fun onInventoryClick(event: ClickInventoryEvent, @First player: Player?) {
        val holder: CarriedInventory<*> = event.targetInventory as? CarriedInventory<*> ?: return
        holder.carrier.ifPresent { carrier: Any? ->
            if (carrier !is BlockCarrier) {
                return@ifPresent
            }
            super.onInventoryClick(
                    toSerialKeyPerson(player!!),
                    toSerialKeyLocation(carrier.location),
                    event.cursorTransaction.final.createStack()
            ) { event.isCancelled = true }
        }
    }
}