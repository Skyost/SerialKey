package fr.skyost.serialkey.sponge.util

import fr.skyost.serialkey.sponge.util.Util.parseString
import org.spongepowered.api.text.Text

/**
 * Represents a simple permission.
 */
class Permission(val permission: String, val role: String, description: String) {
    /**
     * The description.
     */
    val description: Text = parseString(description)
}