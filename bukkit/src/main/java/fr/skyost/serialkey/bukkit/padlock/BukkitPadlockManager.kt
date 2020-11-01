package fr.skyost.serialkey.bukkit.padlock

import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.bukkit.SerialKey
import fr.skyost.serialkey.bukkit.util.DoorUtil
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.padlock.PluginPadlockManager
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.block.DoubleChest
import org.bukkit.inventory.ItemStack

/**
 * The Bukkit padlock manager.
 */
class BukkitPadlockManager(plugin: SerialKey) : PluginPadlockManager<ItemStack, Location>(plugin) {
    override fun fixLocation(location: SerialKeyLocation): Boolean {
        if (hasPadlock(location, false)) {
            return false
        }
        val block = BukkitTypeConverter.toBukkitLocation(location).block
        if (block.state is Chest) {
            val holder = (block.state as Chest).inventory.holder
            if (holder is DoubleChest) {
                val left: SerialKeyLocation = pluginLocationToSerialKeyLocation((holder.leftSide as Chest?)!!.location)
                if (hasPadlock(left, false)) {
                    location.x = left.x
                    location.z = left.z
                    return true
                }
                val right: SerialKeyLocation = pluginLocationToSerialKeyLocation((holder.rightSide as Chest?)!!.location)
                if (hasPadlock(right, false)) {
                    location.x = right.x
                    location.z = right.z
                    return true
                }
            }
            return false
        }
        if (DoorUtil.getInstance(block.blockData) != null) {
            location.y = DoorUtil.getBlockBelow(block)!!.y
            if (hasPadlock(location, false)) {
                return true
            }
            val doubleDoor = DoorUtil.getDoubleDoor(block)
            if (doubleDoor != null) {
                val doubleDoorLocation = doubleDoor.location
                location.x = doubleDoorLocation.blockX
                location.z = doubleDoorLocation.blockZ
            }
            return true
        }
        return false
    }

    override fun pluginLocationToSerialKeyLocation(location: Location): SerialKeyLocation {
        return BukkitTypeConverter.toSerialKeyLocation(location)
    }
}