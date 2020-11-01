package fr.skyost.serialkey.core.util

import java.util.*

/**
 * Contains some useful methods.
 */
object Util {
    /**
     * Returns the map which contains only the specified object.
     *
     * @param map The map.
     * @param objects The object.
     *
     * @return A map which contains only the object (as keys).
     */
	@JvmStatic
	fun <V> keepAll(map: Map<String, V>, objects: Collection<String>): Map<String, V> {
        val result: MutableMap<String, V> = HashMap()
        for (`object` in objects) {
            val chars = CharArray(`object`.length)
            `object`.toCharArray(chars, 0, 0, if (`object`.length > 3) 3 else `object`.length)
            for (c in chars) {
                if (c == ' ') {
                    continue
                }
                val character = c.toString()
                if (map.containsKey(character)) {
                    result[character] = map[character] ?: error("$character should be contained in the map $map.")
                }
            }
        }
        return result
    }

    /**
     * Creates a map.
     *
     * @param keys The keys.
     * @param values The values.
     *
     * @return The map.
     */
	@JvmStatic
	fun <V, K> createMap(keys: Array<K>, values: Array<V>): Map<K, V>? {
        if (keys.size != values.size) {
            return null
        }
        val map: MutableMap<K, V> = HashMap()
        for (i in keys.indices) {
            map[keys[i]] = values[i]
        }
        return map
    }
}