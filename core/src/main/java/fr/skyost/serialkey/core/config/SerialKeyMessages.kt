package fr.skyost.serialkey.core.config;

/**
 * Allows to configure SerialKey messages.
 */

public interface SerialKeyMessages {

	/**
	 * The plugin messages prefix.
	 *
	 * @return The plugin messages prefix.
	 */

	String getPrefix();

	/**
	 * Returns the permission message.
	 *
	 * @return The permission message.
	 */

	String getPermissionMessage();

	/**
	 * Returns the "padlock placed" message.
	 *
	 * @return The "padlock placed" message.
	 */

	String getPadlockPlacedMessage();

	/**
	 * Returns the "padlock removed" message.
	 *
	 * @return The "padlock removed" message.
	 */

	String getPadlockRemovedMessage();

	/**
	 * Returns the "block has a padlock" message.
	 *
	 * @return The "block has a padlock" message.
	 */

	String getBlockHasPadlockMessage();

	/**
	 * Returns the "padlock finder enabled" message.
	 *
	 * @return The "padlock finder enabled" message.
	 */

	String getPadlockFinderEnabledMessage();

	/**
	 * Returns the "padlock finder disabled" message.
	 *
	 * @return The "padlock finder disabled" message.
	 */

	String getPadlockFinderDisabledMessage();

	/**
	 * Returns the "chest protected" message.
	 *
	 * @return The "chest protected" message.
	 */

	String getChestProtectionMessage();

}