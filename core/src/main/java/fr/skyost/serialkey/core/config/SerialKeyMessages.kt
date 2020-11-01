package fr.skyost.serialkey.core.config

/**
 * Allows to configure SerialKey messages.
 */
interface SerialKeyMessages {
    /**
     * The plugin messages prefix.
     *
     * @return The plugin messages prefix.
     */
    val prefix: String

    /**
     * Returns the permission message.
     *
     * @return The permission message.
     */
    val permissionMessage: String

    /**
     * Returns the "padlock placed" message.
     *
     * @return The "padlock placed" message.
     */
    val padlockPlacedMessage: String

    /**
     * Returns the "padlock removed" message.
     *
     * @return The "padlock removed" message.
     */
    val padlockRemovedMessage: String

    /**
     * Returns the "block has a padlock" message.
     *
     * @return The "block has a padlock" message.
     */
    val blockHasPadlockMessage: String

    /**
     * Returns the "padlock finder enabled" message.
     *
     * @return The "padlock finder enabled" message.
     */
    val padlockFinderEnabledMessage: String

    /**
     * Returns the "padlock finder disabled" message.
     *
     * @return The "padlock finder disabled" message.
     */
    val padlockFinderDisabledMessage: String

    /**
     * Returns the "chest protected" message.
     *
     * @return The "chest protected" message.
     */
    val chestProtectionMessage: String
}