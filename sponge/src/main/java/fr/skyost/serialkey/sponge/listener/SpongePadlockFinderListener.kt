package fr.skyost.serialkey.sponge.listener

import com.flowpowered.math.vector.Vector3d
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.listener.PadlockFinderListener
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyLocation
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyPerson
import fr.skyost.serialkey.sponge.util.Util.blankItem
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * A listener that allows to listen padlock finder related events.
 */
class SpongePadlockFinderListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : PadlockFinderListener<ItemStack, Location<World>>(plugin) {
    @Listener(order = Order.LATE)
    fun onPlayerRightClick(event: InteractBlockEvent.Secondary, @First player: Player) {
        val spawn: SerialKeyLocation = toSerialKeyLocation(player.world.spawnLocation)
        super.onPlayerRightClick(
                player.getItemInHand(event.handType).orElse(blankItem()),
                toSerialKeyPerson(player),
                spawn,
                player.get(Keys.TARGETED_LOCATION).map { position: Vector3d -> SerialKeyLocation(player.world.name, position.x.toInt(), position.y.toInt(), position.z.toInt()) }.orElse(spawn),
                { location: SerialKeyLocation -> player.offer(Keys.TARGETED_LOCATION, Vector3d(location.x.toDouble(), location.y.toDouble(), location.z.toDouble())) },
                { event.isCancelled = true }
        )
    }
}