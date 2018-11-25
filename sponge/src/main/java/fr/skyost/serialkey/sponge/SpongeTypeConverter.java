package fr.skyost.serialkey.sponge;

import fr.skyost.serialkey.core.object.PersonIdentity;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;
import fr.skyost.serialkey.sponge.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * A class that allows to convert core objects to Sponge objects (and vice-versa).
 */

public class SpongeTypeConverter {

	/**
	 * Converts a SerialKey location to a Sponge location.
	 *
	 * @param location The SerialKey location.
	 *
	 * @return The Sponge location.
	 */

	public static Location<World> toSpongeLocation(final SerialKeyLocation location) {
		final Optional<World> optional = Sponge.getServer().getWorld(location.getWorld());
		return optional.map(world -> new Location<>(world, location.getX(), location.getY(), location.getZ())).orElse(null);
	}

	/**
	 * Converts a Sponge location to a SerialKey location.
	 *
	 * @param location The Sponge location.
	 *
	 * @return The SerialKey location.
	 */

	public static SerialKeyLocation toSerialKeyLocation(final Location<World> location) {
		return new SerialKeyLocation(location.getExtent().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	/**
	 * Converts a SerialKey person to a Sponge command sender.
	 *
	 * @param person The SerialKey person.
	 *
	 * @return The Sponge command sender.
	 */

	public static CommandSource toSpongePerson(final SerialKeyPerson person) {
		final PersonIdentity identity = person.getIdentity();
		if(identity.getType() == PersonIdentity.Type.CONSOLE) {
			return Sponge.getServer().getConsole();
		}

		return Sponge.getServer().getPlayer(identity.getName()).orElse(null);
	}

	/**
	 * Converts a Sponge command sender to a SerialKey person.
	 *
	 * @param sender Sponge command sender.
	 *
	 * @return The SerialKey person.
	 */

	public static SerialKeyPerson toSerialKeyPerson(final CommandSource sender) {
		return new SpongePerson(sender);
	}

	/**
	 * Represents a Sponge person.
	 */

	private static class SpongePerson implements SerialKeyPerson {

		/**
		 * The command source instance.
		 */

		private final CommandSource sender;

		/**
		 * The identity.
		 */

		private final PersonIdentity identity;

		/**
		 * Creates a new Sponge person instance.
		 *
		 * @param sender The command source instance.
		 */

		public SpongePerson(final CommandSource sender) {
			this.sender = sender;
			this.identity = new PersonIdentity(sender instanceof Player ? PersonIdentity.Type.PLAYER : PersonIdentity.Type.CONSOLE, sender.getName());
		}

		@Override
		public PersonIdentity getIdentity() {
			return identity;
		}

		@Override
		public void sendMessage(final String message) {
			sender.sendMessage(Util.parseString(message));
		}

		@Override
		public boolean hasPermission(final String permission) {
			return sender.hasPermission(permission);
		}

	}

}