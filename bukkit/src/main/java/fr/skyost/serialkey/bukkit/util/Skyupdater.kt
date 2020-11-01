package fr.skyost.serialkey.bukkit.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import com.google.common.base.Joiner
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Level
import java.util.regex.Pattern

/**
 * A simple auto-updater.
 * <br></br>Please follow this link to read more about checking for updates in your plugin : https://www.skyost.eu/Skyupdater.txt.
 * <br></br><br></br>Thanks to Gravity for his updater (this file uses some parts of his code) !
 *
 * Please note that it needs minimal-json to work.
 *
 * @author [Skyost](https://www.skyost.eu).
 */
class Skyupdater(private val plugin: Plugin, private val id: Int, private val pluginFile: File, private val download: Boolean, private val announce: Boolean) {
    private val updateFolder: File = Bukkit.getUpdateFolderFile()
    private lateinit var url: URL
    private val config = YamlConfiguration()
    private var result = Result.SUCCESS
    private var updateData: JsonObject? = null
    private var response: String? = null
    private var updaterThread: Thread? = null

    /**
     * Represents the updater's result.
     */
    enum class Result {
        /**
         * A new version has been found, downloaded and will be loaded at the next server reload / restart.
         */
        SUCCESS,

        /**
         * A new version has been found but nothing was downloaded.
         */
        UPDATE_AVAILABLE,

        /**
         * No update found.
         */
        NO_UPDATE,

        /**
         * The updater is disabled.
         */
        DISABLED,

        /**
         * An error occurred.
         */
        ERROR
    }

    /**
     * Info type enumeration.
     */
    enum class InfoType(val jSONKey: String) {
        /**
         * Gets the download URL.
         */
        DOWNLOAD_URL("downloadUrl"),

        /**
         * Gets the file name.
         */
        FILE_NAME("fileName"),

        /**
         * Gets the game version.
         */
        GAME_VERSION("gameVersion"),

        /**
         * Gets the file's title.
         */
        FILE_TITLE("name"),

        /**
         * Gets the release type.
         */
        RELEASE_TYPE("releaseType");

    }

    /**
     * Gets the result of Skyupdater.
     *
     * @return The result of the update process.
     */
    fun getResult(): Result {
        waitForThread()
        return result
    }

    /**
     * Gets information about the latest file.
     *
     * @param type The kind of information you want.
     *
     * @return The information you want.
     */
    fun getLatestFileInfo(type: InfoType): String {
        waitForThread()
        return updateData!![type.jSONKey].asString()
    }

    /**
     * Gets raw data about the latest file.
     *
     * @return A JSON object which contains every of the update process.
     */
    val latestFileData: JsonObject?
        get() {
            waitForThread()
            return updateData
        }

    /**
     * Downloads a file.
     *
     * @param site The URL of the file you want to download.
     * @param pathTo The path where you want the file to be downloaded.
     *
     * @return **true**If the download was a success.
     * falseIf there is an error during the download.
     */
    private fun download(site: String, pathTo: File): Boolean {
        try {
            val connection = URL(site).openConnection() as HttpURLConnection
            connection.addRequestProperty("User-Agent", "Skyupdater v$version")
            response = connection.responseCode.toString() + " " + connection.responseMessage
            if (!response!!.startsWith("2")) {
                log(Level.WARNING, "Bad response : '$response' when trying to download the update.")
                result = Result.ERROR
                return false
            }
            val data = ByteArray(1024)
            val size = connection.contentLengthLong
            val koSize = size / 1000
            val inputStream = connection.inputStream
            val fileOutputStream = FileOutputStream(pathTo)
            val bufferedOutputStream = BufferedOutputStream(fileOutputStream, 1024)
            var lastPercent = 0
            var totalDataRead = 0f
            var i: Int
            while (inputStream.read(data, 0, 1024).also { i = it } >= 0) {
                totalDataRead += i.toFloat()
                bufferedOutputStream.write(data, 0, i)
                if (announce) {
                    val percent = ((totalDataRead * 100).toLong() / size).toInt()
                    if (lastPercent != percent) {
                        lastPercent = percent
                        log(Level.INFO, percent.toString() + "% of " + koSize + "ko...")
                    }
                }
            }
            bufferedOutputStream.close()
            fileOutputStream.close()
            inputStream.close()
            return true
        } catch (ex: Exception) {
            log(Level.SEVERE, "Exception '$ex' occurred while downloading update. Please check your network connection.")
            result = Result.ERROR
        }
        return false
    }

    /**
     * Logs a message if "announce" is set to true.
     *
     * @param level The level of logging.
     * @param message The message.
     */
    private fun log(level: Level, message: String) {
        if (announce) {
            Bukkit.getLogger().log(level, "[Skyupdater] $message")
        }
    }

    /**
     * As the result of Updater output depends on the thread's completion,
     * <br></br>it is necessary to wait for the thread to finish before allowing anyone to check the result.
     *
     * @author **Gravity** from his Updater.
     */
    private fun waitForThread() {
        if (updaterThread != null && updaterThread!!.isAlive) {
            try {
                updaterThread!!.join()
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }

    private inner class UpdaterThread : Runnable {
        override fun run() {
            try {
                val pluginName = plugin.name.replace("_", " ")
                val connection = url.openConnection() as HttpURLConnection
                connection.addRequestProperty("User-Agent", "Skyupdater v$version")
                val apiKey = config.getString("api-key")
                if (apiKey != null && apiKey != "NONE") {
                    connection.addRequestProperty("X-API-Key", apiKey)
                }
                response = connection.responseCode.toString() + " " + connection.responseMessage
                if (!response!!.startsWith("2")) {
                    log(Level.INFO, "Bad response : '" + response + if (response!!.startsWith("402")) "'. Maybe your API Key is invalid ?" else "'.")
                    result = Result.ERROR
                    return
                }
                val inputStreamReader = InputStreamReader(connection.inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val response = bufferedReader.readLine()
                if (response != null && response != "[]") {
                    val jsonArray = Json.parse(response).asArray()
                    updateData = jsonArray[jsonArray.size() - 1].asObject()
                    if (compareVersions(getLatestFileInfo(InfoType.FILE_TITLE).split("^v|[\\s_-]v").toTypedArray()[1].split(" ").toTypedArray()[0], plugin.description.version) && getLatestFileInfo(InfoType.DOWNLOAD_URL).toLowerCase().endsWith(".jar")) {
                        result = Result.UPDATE_AVAILABLE
                        if (download) {
                            log(Level.INFO, "Downloading a new update : " + getLatestFileInfo(InfoType.FILE_TITLE) + "...")
                            if (download(getLatestFileInfo(InfoType.DOWNLOAD_URL), File(updateFolder, pluginFile.name))) {
                                result = Result.SUCCESS
                                log(Level.INFO, "The update of '$pluginName' has been downloaded and installed. It will be loaded at the next server load / reload.")
                            } else {
                                result = Result.ERROR
                            }
                        } else {
                            log(Level.INFO, "An update has been found for '$pluginName' but nothing was downloaded.")
                        }
                        return
                    } else {
                        result = Result.NO_UPDATE
                        log(Level.INFO, "No update found for '$pluginName'.")
                    }
                } else {
                    log(Level.SEVERE, "The ID '" + id + "' was not found (or no files found for this project) ! Maybe the author(s) (" + Joiner.on(", ").join(plugin.description.authors) + ") of '" + pluginName + "' has/have misconfigured his/their plugin ?")
                    result = Result.ERROR
                }
                bufferedReader.close()
                inputStreamReader.close()
            } catch (ex: Exception) {
                log(Level.SEVERE, "Exception '$ex'. Please check your network connection.")
                result = Result.ERROR
            }
        }
    }

    companion object {
        /**
         * Gets the version of Skyupdater.
         *
         * @return The version of Skyupdater.
         */
        const val version = "0.6"

        /**
         * Compares two versions.
         *
         * @param versionTo The version you want to compare to.
         * @param versionWith The version you want to compare with.
         *
         * @return **true** If **versionTo** is inferior than **versionWith**.
         * <br></br>**false** If **versionTo** is superior or equals to **versionWith**.
         */
        fun compareVersions(versionTo: String, versionWith: String): Boolean {
            return normalisedVersion(versionTo, ".", 4) > normalisedVersion(versionWith, ".", 4)
        }

        /**
         * Gets the formatted name of a version.
         * <br></br>Used for the method **compareVersions(...)** of this class.
         *
         * @param version The version you want to addLocation.
         * @param separator The separator between the numbers of this version.
         * @param maxWidth The max width of the formatted version.
         *
         * @return The formatted version of your version.
         *
         * @author Peter Lawrey.
         */
        private fun normalisedVersion(version: String, separator: String, maxWidth: Int): String {
            val stringBuilder = StringBuilder()
            for (normalised in Pattern.compile(separator, Pattern.LITERAL).split(version)) {
                stringBuilder.append(String.format("%" + maxWidth + 's', normalised))
            }
            return stringBuilder.toString()
        }
    }

    init {
        if (!updateFolder.exists()) {
            updateFolder.mkdir()
        }
        val skyupdaterFolder = File(plugin.dataFolder.parentFile, "Skyupdater")
        if (!skyupdaterFolder.exists()) {
            skyupdaterFolder.mkdir()
        }
        val lineSeparator = System.lineSeparator()
        val header = StringBuilder()
        header.append("Skyupdater configuration - https://www.skyost.eu/Skyupdater.txt$lineSeparator$lineSeparator")
        header.append("What is Skyupdater ?$lineSeparator")
        header.append("Skyupdater is a simple updater created by Skyost (https://www.skyost.eu). It aims to auto-update Bukkit Plugins.$lineSeparator$lineSeparator")
        header.append("What happens during the update process ?$lineSeparator")
        header.append("1. Connection to curseforge.com.$lineSeparator")
        header.append("2. Plugin version compared against version on curseforge.com.$lineSeparator")
        header.append("3. Downloading of the plugin from curseforge.com if a newer version is found.$lineSeparator$lineSeparator")
        header.append("So what is this file ?$lineSeparator")
        header.append("This file is just a config file for this auto-updater.$lineSeparator$lineSeparator")
        header.append("Configuration :$lineSeparator")
        header.append("'enable': Choose if you want to enable the auto-updater.$lineSeparator")
        header.append("'api-key': OPTIONAL. Your BukkitDev API Key.$lineSeparator$lineSeparator")
        header.append("Good game, I hope you will enjoy your plugins always up-to-date ;)$lineSeparator")
        val configFile = File(skyupdaterFolder, "skyupdater.yml")
        if (!configFile.exists()) {
            configFile.createNewFile()
            config.options().header(header.toString())
            config["enable"] = true
            config["api-key"] = "NONE"
            config.save(configFile)
        }
        config.load(configFile)
        if (!config.getBoolean("enable")) {
            result = Result.DISABLED
            log(Level.INFO, "Skyupdater is disabled.")
        }
        else {
            url = URL("https://api.curseforge.com/servermods/files?projectIds=$id")
            updaterThread = Thread(UpdaterThread())
            updaterThread!!.start()
        }
    }
}