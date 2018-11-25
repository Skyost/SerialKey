package fr.skyost.serialkey.bukkit;

import fr.skyost.serialkey.core.object.PersonIdentity;
import fr.skyost.serialkey.core.object.SerialKeyLocation;
import fr.skyost.serialkey.core.object.SerialKeyPerson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A class that allows to convert core objects to Bukkit objects (and vice-versa).
 */

public class BukkitTypeConverter {

	/**
	 * Converts a SerialKey location to a Bukkit location.
	 *
	 * @param location The SerialKey location.
	 *
	 * @return The Bukkit location.
	 */

	public static Location toBukkitLocation(final SerialKeyLocation location) {
		return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
	}

	/**
	 * Converts a Bukkit location to a SerialKey location.
	 *
	 * @param location The Bukkit location.
	 *
	 * @return The SerialKey location.
	 */

	public static SerialKeyLocation toSerialKeyLocation(final Location location) {
		return new SerialKeyLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	/**
	 * Converts a SerialKey person to a Bukkit command sender.
	 *
	 * @param person The SerialKey person.
	 *
	 * @return The Bukkit command sender.
	 */

	public static CommandSender toBukkitPerson(final SerialKeyPerson person) {
		final PersonIdentity identity = person.getIdentity();
		if(identity.getType() == PersonIdentity.Type.CONSOLE) {
			return Bukkit.getConsoleSender();
		}

		return Bukkit.getPlayer(identity.getName());
	}

	/**
	 * Converts a Bukkit command sender to a SerialKey person.
	 *
	 * @param sender Bukkit command sender.
	 *
	 * @return The SerialKey person.
	 */

	public static SerialKeyPerson toSerialKeyPerson(final CommandSender sender) {
		return new BukkitPerson(sender);
	}

	/**
	 * Represents a Bukkit person.
	 */

	private static class BukkitPerson implements SerialKeyPerson {

		/**
		 * The sender instance.
		 */

		private final CommandSender sender;

		/**
		 * The identity.
		 */

		private final PersonIdentity identity;

		/**
		 * Creates a new Bukkit person instance.
		 *
		 * @param sender The sender instance.
		 */

		BukkitPerson(final CommandSender sender) {
			this.sender = sender;
			this.identity = new PersonIdentity(sender instanceof Player ? PersonIdentity.Type.PLAYER : PersonIdentity.Type.CONSOLE, sender.getName());
		}

		@Override
		public PersonIdentity getIdentity() {
			return identity;
		}

		@Override
		public void sendMessage(final String message) {
			sender.sendMessage(message);
		}

		@Override
		public boolean hasPermission(final String permission) {
			return sender.hasPermission(permission);
		}

	}

}