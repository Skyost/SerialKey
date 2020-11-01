package fr.skyost.serialkey.core.`object`

/**
 * Represents a person.
 */
interface SerialKeyPerson {
    /**
     * Returns the person identity.
     *
     * @return The identity.
     */
    val identity: PersonIdentity

    /**
     * Sends a message to the current person.
     *
     * @param message The message.
     */
    fun sendMessage(message: String)

    /**
     * Returns whether the person has the specified permission.
     *
     * @param permission The permission.
     *
     * @return Whether the person has the specified permission.
     */
    fun hasPermission(permission: String): Boolean
}