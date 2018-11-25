package fr.skyost.serialkey.core.object;

/**
 * Represents a person.
 */

public interface SerialKeyPerson {

	/**
	 * Returns the person identity.
	 *
	 * @return The identity.
	 */

	PersonIdentity getIdentity();

	/**
	 * Sends a message to the current person.
	 *
	 * @param message The message.
	 */

	void sendMessage(final String message);

	/**
	 * Returns whether the person has the specified permission.
	 *
	 * @param permission The permission.
	 *
	 * @return Whether the person has the specified permission.
	 */

	boolean hasPermission(final String permission);

}