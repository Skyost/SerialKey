package fr.skyost.serialkey.core.util

/**
 * [ROT47](https://en.wikipedia.org/wiki/ROT13#Variants) algorithm implementation.
 */
object ROT47 {
    /**
     * Rotates a string.
     *
     * @param value The string.
     *
     * @return The rotated string.
     */
	@JvmStatic
	fun rotate(value: String): String {
        val result = StringBuilder()
        val n = value.length
        for (i in 0 until n) {
            var c: Char = value[i]
            if (c != ' ') {
                c += 47
                if (c > '~') {
                    c -= 94
                }
            }
            result.append(c)
        }
        return result.toString()
    }
}