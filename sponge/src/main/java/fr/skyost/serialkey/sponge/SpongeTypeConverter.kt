package fr.skyost.serialkey.sponge

import fr.skyost.serialkey.core.`object`.PersonIdentity
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import fr.skyost.serialkey.core.`object`.SerialKeyPerson
import fr.skyost.serialkey.sponge.util.Util.parseString
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

/**
 * A class that allows to convert core objects to Sponge objects (and vice-versa).
 */
object SpongeTypeConverter {
    /**
     * Converts a SerialKey location to a Sponge location.
     *
     * @param location The SerialKey location.
     *
     * @return The Sponge location.
     */
    @JvmStatic
    fun toSpongeLocation(location: SerialKeyLocation): Location<World> {
        val optional: Optional<World> = Sponge.getServer().getWorld(location.world ?: Sponge.getServer().defaultWorldName)
        return optional.map { world: World -> Location(world, location.x, location.y, location.z) }.orElse(null)
    }

    /**
     * Converts a Sponge location to a SerialKey location.
     *
     * @param location The Sponge location.
     *
     * @return The SerialKey location.
     */
    @JvmStatic
    fun toSerialKeyLocation(location: Location<World>): SerialKeyLocation {
        return SerialKeyLocation(location.extent.name, location.blockX, location.blockY, location.blockZ)
    }

    /**
     * Converts a SerialKey person to a Sponge command sender.
     *
     * @param person The SerialKey person.
     *
     * @return The Sponge command sender.
     */
    fun toSpongePerson(person: SerialKeyPerson): CommandSource {
        val identity: PersonIdentity = person.identity
        return if (identity.type === PersonIdentity.Type.CONSOLE) {
            Sponge.getServer().console
        } else Sponge.getServer().getPlayer(identity.name).orElse(null)
    }

    /**
     * Converts a Sponge command sender to a SerialKey person.
     *
     * @param sender Sponge command sender.
     *
     * @return The SerialKey person.
     */
    @JvmStatic
    fun toSerialKeyPerson(sender: CommandSource): SerialKeyPerson {
        return SpongePerson(sender)
    }

    /**
     * Represents a Sponge person.
     */
    private class SpongePerson(val sender: CommandSource, override val identity: PersonIdentity = PersonIdentity(if (sender is Player) PersonIdentity.Type.PLAYER else PersonIdentity.Type.CONSOLE, sender.name)) : SerialKeyPerson {
        override fun sendMessage(message: String) {
            sender.sendMessage(parseString(message))
        }

        override fun hasPermission(permission: String): Boolean {
            return sender.hasPermission(permission)
        }
    }
}