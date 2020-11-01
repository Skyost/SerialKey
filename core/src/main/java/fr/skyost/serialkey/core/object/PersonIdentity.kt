package fr.skyost.serialkey.core.`object`

/**
 * Represents a SerialKey person identity.
 */
data class PersonIdentity(val type: Type, val name: String) {
    /**
     * Identity types.
     */
    enum class Type {
        /**
         * Console type.
         */
        CONSOLE,

        /**
         * Player type.
         */
        PLAYER
    }
}