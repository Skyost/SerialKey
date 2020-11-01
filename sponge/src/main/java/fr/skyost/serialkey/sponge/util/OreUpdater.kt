package fr.skyost.serialkey.sponge.util

import com.google.gson.JsonParser
import fr.skyost.serialkey.sponge.BuildConfig
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Ore update checker.
 */
class OreUpdater(private val logger: Logger) : Thread() {
    override fun run() {
        try {
            logger.info("Checking for updates...")

            // Let's get the plugin.
            val optionalPluginContainer = Sponge.getPluginManager().getPlugin(PLUGIN_ID)
            if (!optionalPluginContainer.isPresent) {
                throw NullPointerException("No plugin found for \"$PLUGIN_ID\".")
            }

            // We have to get the plugin's version.
            val optionalLocalVersion = optionalPluginContainer.get().version
            if (!optionalLocalVersion.isPresent) {
                return
            }

            // Now we can request the remote version.
            val url = URL(String.format("https://ore.spongepowered.org/api/v1/projects/%s", PLUGIN_ID))
            val version = JsonParser().parse(httpGet(url)).asJsonObject["recommended"].asJsonObject["name"].asString

            // And we can compare them.
            if (versionCompare(optionalLocalVersion.get(), version) >= 0) {
                logger.info("No update found.")
                return
            }
            logger.info("Found an update !")
            logger.info("Head to \"https://ore.spongepowered.org/api/projects/$PLUGIN_ID/versions/recommended/download\" to download $version...")
        } catch (ex: Exception) {
            logger.error("Cannot check for updates :", ex)
        }
    }

    companion object {
        /**
         * Plugin ID.
         */
        private const val PLUGIN_ID = BuildConfig.PLUGIN_ID

        /**
         * Compares two version strings.
         *
         * Use this instead of String.compareTo() for a non-lexicographical
         * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
         *
         * @param str1 a string of ordinal numbers separated by decimal points.
         * @param str2 a string of ordinal numbers separated by decimal points.
         * @return The result is a negative integer if str1 is _numerically_ less than str2.
         * The result is a positive integer if str1 is _numerically_ greater than str2.
         * The result is zero if the strings are _numerically_ equal.
         *
         * @author Alex Gitelman.
         */
        private fun versionCompare(str1: String, str2: String): Int {
            val vals1 = str1.split("\\.").toTypedArray()
            val vals2 = str2.split("\\.").toTypedArray()
            var i = 0
            // set index to first non-equal ordinal or length of shortest version string
            while (i < vals1.size && i < vals2.size && vals1[i] == vals2[i]) {
                i++
            }
            // compare first non-equal ordinal number
            if (i < vals1.size && i < vals2.size) {
                val diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]))
                return Integer.signum(diff)
            }
            // the strings are equal or one string is a substring of the other
            // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
            return Integer.signum(vals1.size - vals2.size)
        }

        /**
         * Sends an HTTP GET request.
         *
         * @param url The URL.
         *
         * @return The response body.
         *
         * @throws IOException If any I/O exception occurs.
         */
        @Throws(IOException::class)
        private fun httpGet(url: URL): String {
            val result = StringBuilder()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }
            reader.close()
            return result.toString()
        }
    }
}