package fr.skyost.serialkey.sponge.config

import fr.skyost.serialkey.core.config.SerialKeyData
import java.nio.file.Path
import java.util.*

/**
 * The plugin data class.
 */
class SpongePluginData(file: Path) : SpongeConfig(file, listOf("SerialKey Data")), SerialKeyData {
    @ConfigOptions(name = "padlocks")
    var padlocks: MutableList<String> = ArrayList()
    override fun getData(): MutableList<String> {
        return padlocks
    }
}