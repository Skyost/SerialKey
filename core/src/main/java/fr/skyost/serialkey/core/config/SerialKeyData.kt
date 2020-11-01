package fr.skyost.serialkey.core.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import fr.skyost.serialkey.core.`object`.SerialKeyLocation
import java.util.*

/**
 * Allows to collect and save padlocks.
 */
interface SerialKeyData {
    /**
     * Returns the raw data.
     *
     * @return The raw data.
     */
    fun getData(): MutableList<String>

    /**
     * Returns the padlocks.
     *
     * @return The padlocks.
     */

    fun getPadlocks(): HashSet<SerialKeyLocation> {
        val result: HashSet<SerialKeyLocation> = HashSet<SerialKeyLocation>()
        for (padlock in getData()) {
            result.add(locationFromJson(padlock))
        }
        return result
    }

    /**
     * Adds a padlock.
     *
     * @param location The padlock location.
     */
    fun addPadlock(location: SerialKeyLocation) {
        getData().add(locationToJson(location))
    }

    /**
     * Removes a padlock.
     *
     * @param location The padlock location.
     *
     * @return Whether a padlock has been removed.
     */
    fun removePadlock(location: SerialKeyLocation): Boolean {
        val data = getData()
        for (padlock in ArrayList(data)) {
            if (locationFromJson(padlock).equals(location)) {
                data.remove(padlock)
                return true
            }
        }
        return false
    }

    /**
     * Converts a JSON String to a SerialKey location.
     *
     * @param json The JSON String.
     *
     * @return The SerialKey location.
     */
    fun locationFromJson(json: String): SerialKeyLocation {
        val representation = JsonParser().parse(json).asJsonObject
        return SerialKeyLocation(representation["world"].asString, representation["x"].asInt, representation["y"].asInt, representation["z"].asInt)
    }

    /**
     * Converts a SerialKey location to a JSON String.
     *
     * @param location The SerialKey location.
     *
     * @return The JSON String.
     */
    fun locationToJson(location: SerialKeyLocation): String {
        val representation = JsonObject()
        representation.add("world", JsonPrimitive(location.world))
        representation.add("x", JsonPrimitive(location.x))
        representation.add("y", JsonPrimitive(location.y))
        representation.add("z", JsonPrimitive(location.z))
        return representation.toString()
    }
}