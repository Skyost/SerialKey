package fr.skyost.serialkey.sponge.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.listener.BlocksListener
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.Creature
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.block.ChangeBlockEvent.Place
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.explosion.Explosion

/**
 * A listener that allows to listen blocks related events.
 */
class SpongeBlocksListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : BlocksListener<ItemStack, Location<World>>(plugin) {
    @Listener(order = Order.LATE)
    fun onBlockPlace(event: Place, @First player: Player) {
        player.getItemInHand(HandTypes.MAIN_HAND).ifPresent { item: ItemStack -> super.onBlockPlace(item) { event.isCancelled = true } }
    }

    @Listener(order = Order.LATE)
    fun onBlockBreak(event: ChangeBlockEvent, @First creature: Creature?) {
        val transactions = event.transactions
        for (transaction in transactions) {
            if (super.onBlockBreakByCreature(transaction.original.location.orElse(null)) { event.isCancelled = true }) {
                break
            }
        }
    }

    @Listener(order = Order.LATE)
    fun onBlockExplode(event: ChangeBlockEvent, @First explosion: Explosion?) {
        val transactions = event.transactions
        for (transaction in transactions) {
            if (super.onBlockExplode(transaction.original.location.orElse(null)) { event.isCancelled = true }) {
                break
            }
        }
    }

    @Listener(order = Order.LATE)
    fun onNotifyNeighborBlock(event: NotifyNeighborBlockEvent, @First source: LocatableBlock) {
        val location = source.location
        val directions: Set<Direction> = event.neighbors.keys
        for (direction in directions) {
            if (super.onBlockPoweredByRedstone(location.getBlockRelative(direction)) { event.isCancelled = true }) {
                break
            }
        }
    }
}