package fr.skyost.serialkey.sponge.listener

import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import fr.skyost.serialkey.core.listener.HopperListener
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyLocation
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyPerson
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSpongeLocation
import fr.skyost.serialkey.sponge.util.ChestUtil
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.Transaction
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.ChangeBlockEvent.Place
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.function.Consumer

/**
 * A listener that allows to listen hoppers related events.
 */
class SpongeHopperListener(plugin: SerialKeyPlugin<ItemStack, Location<World>>) : HopperListener<ItemStack, Location<World>>(plugin) {
    @Listener(order = Order.LATE)
    fun onBlockPlace(event: Place, @First player: Player?) {
        val person: SerialKeyPerson = toSerialKeyPerson(player!!)
        event.transactions.forEach(Consumer { transaction: Transaction<BlockSnapshot> ->
            val block = transaction.final
            if (block.state.type !== BlockTypes.HOPPER) {
                return@Consumer
            }
            block.location.ifPresent { location: Location<World> -> super.onBlockPlace(toSerialKeyLocation(location), person) { transaction.setValid(false) } }
        })
    }

    override fun isChest(location: SerialKeyLocation): Boolean {
        return ChestUtil.isChest(toSpongeLocation(location).blockType)
    }
}