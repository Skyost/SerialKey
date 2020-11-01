package fr.skyost.serialkey.bukkit

import fr.skyost.serialkey.core.`object`.PersonIdentity
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


/**
 * A class that allows to convert core objects to Bukkit objects (and vice-versa).
 */
object BukkitTypeConverter {
    /**
     * Converts a SerialKey location to a Bukkit location.
     *
     * @param location The SerialKey location.
     *
     * @return The Bukkit location.
     */
    fun toBukkitLocation(location: SerialKeyLocation): Location {
        return Location(if (location.world != null) Bukkit.getWorld(location.world!!) else Bukkit.getWorlds()[0], location.x.toDouble(), location.y.toDouble(), location.z.toDouble())
    }

    /**
     * Converts a Bukkit location to a SerialKey location.
     *
     * @param location The Bukkit location.
     *
     * @return The SerialKey location.
     */
    fun toSerialKeyLocation(location: Location): SerialKeyLocation {
        return SerialKeyLocation(location.world!!.name, location.blockX, location.blockY, location.blockZ)
    }

    /**
     * Converts a SerialKey person to a Bukkit command sender.
     *
     * @param person The SerialKey person.
     *
     * @return The Bukkit command sender.
     */
    fun toBukkitPerson(person: SerialKeyPerson): CommandSender {
        val identity: PersonIdentity = person.identity
        return if (identity.type === PersonIdentity.Type.CONSOLE) {
            Bukkit.getConsoleSender()
        } else Bukkit.getPlayer(identity.name)!!
    }

    /**
     * Converts a Bukkit command sender to a SerialKey person.
     *
     * @param sender Bukkit command sender.
     *
     * @return The SerialKey person.
     */
    fun toSerialKeyPerson(sender: CommandSender): SerialKeyPerson {
        return BukkitPerson(sender)
    }

    /**
     * Represents a Bukkit person.
     */
    private class BukkitPerson(val sender: CommandSender, override val identity: PersonIdentity = PersonIdentity(if (sender is Player) PersonIdentity.Type.PLAYER else PersonIdentity.Type.CONSOLE, sender.name)) : SerialKeyPerson {
        override fun sendMessage(message: String) {
            sender.sendMessage(message)
        }

        override fun hasPermission(permission: String): Boolean {
            return sender.hasPermission(permission)
        }
    }
}