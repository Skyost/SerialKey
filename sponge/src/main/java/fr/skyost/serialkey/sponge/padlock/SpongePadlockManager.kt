package fr.skyost.serialkey.sponge.padlock

import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.padlock.PluginPadlockManager
import fr.skyost.serialkey.sponge.SerialKey
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSerialKeyLocation
import fr.skyost.serialkey.sponge.SpongeTypeConverter.toSpongeLocation
import fr.skyost.serialkey.sponge.util.ChestUtil.getChest
import fr.skyost.serialkey.sponge.util.DoorUtil.getBlockBelow
import fr.skyost.serialkey.sponge.util.DoorUtil.getDoubleDoor
import fr.skyost.serialkey.sponge.util.DoorUtil.isDoor
import org.spongepowered.api.block.tileentity.carrier.Chest
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * The Sponge padlock manager.
 */
class SpongePadlockManager(plugin: SerialKey) : PluginPadlockManager<ItemStack, Location<World>>(plugin) {
    override fun fixLocation(location: SerialKeyLocation): Boolean {
        if (hasPadlock(location, false)) {
            return false
        }
        val spongeLocation = toSpongeLocation(location)
        var chest: Chest
        if (getChest(spongeLocation).also { chest = it!! } != null) {
            val connectedChests = chest.connectedChests
            for (connectedChest in connectedChests) {
                val connectedLocation = connectedChest.location
                if (hasPadlock(pluginLocationToSerialKeyLocation(connectedLocation), false)) {
                    location.x = connectedLocation.blockX
                    location.z = connectedLocation.blockZ
                    return true
                }
            }
        } else if (isDoor(spongeLocation)) {
            location.y = getBlockBelow(spongeLocation)!!.blockY
            if (hasPadlock(location, false)) {
                return true
            }
            val doubleDoor = getDoubleDoor(toSpongeLocation(location))
            if (doubleDoor != null) {
                location.x = doubleDoor.blockX
                location.z = doubleDoor.blockZ
                return true
            }
        }
        return false
    }

    override fun pluginLocationToSerialKeyLocation(location: Location<World>): SerialKeyLocation {
        return toSerialKeyLocation(location)
    }
}