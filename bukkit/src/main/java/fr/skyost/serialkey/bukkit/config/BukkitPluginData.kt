package fr.skyost.serialkey.bukkit.config

import fr.skyost.serialkey.bukkit.util.Skyoconfig
import fr.skyost.serialkey.core.config.SerialKeyData
import java.io.File
import java.util.*

/**
 * The plugin data class.
 */
class BukkitPluginData(dataFolder: File) : Skyoconfig(File(dataFolder, "data.yml"), listOf("SerialKey Data")), SerialKeyData {
    @ConfigOptions(name = "padlocks")
    var padlocks: MutableList<String> = ArrayList()
    override fun getData(): MutableList<String> {
        return padlocks
    }
}