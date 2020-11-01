package fr.skyost.serialkey.bukkit.config

import fr.skyost.serialkey.bukkit.util.Skyoconfig
import fr.skyost.serialkey.core.config.SerialKeyConfig
import org.bukkit.ChatColor
import org.bukkit.Material
import java.io.File
import java.util.*

/**
 * The plugin configuration class.
 */
class BukkitPluginConfig(dataFolder: File) : Skyoconfig(File(dataFolder, "config.yml"), listOf("SerialKey Configuration")), SerialKeyConfig {
    @ConfigOptions(name = "enable.updater")
    var enableUpdater = true

    @ConfigOptions(name = "enable.metrics")
    var enableMetrics = true

    @ConfigOptions(name = "options.reusable-keys")
    override var reusableKeys = true

    @ConfigOptions(name = "options.disable-hoppers")
    var disableHoppers = true

    @ConfigOptions(name = "options.encrypt-lore")
    override var encryptLore = false

    @ConfigOptions(name = "options.can-rename-items")
    override var canRenameItems = false

    @ConfigOptions(name = "options.allow-lost-chests")
    var allowLostChests = true

    @ConfigOptions(name = "key.material")
    var keyMaterial = Material.TRIPWIRE_HOOK

    @ConfigOptions(name = "key.name")
    var keyName = ChatColor.GOLD.toString() + "Key"

    @ConfigOptions(name = "key.recipe")
    override var keyShape = listOf("A", "B")

    @ConfigOptions(name = "master-key.material")
    var masterKeyMaterial = Material.NAME_TAG

    @ConfigOptions(name = "master-key.name")
    var masterKeyName = ChatColor.DARK_PURPLE.toString() + "Master Key"

    @ConfigOptions(name = "master-key.recipe")
    override var masterKeyShape = listOf("C", "B")

    @ConfigOptions(name = "bunch-of-keys.material")
    var bunchOfKeysMaterial = Material.NAME_TAG

    @ConfigOptions(name = "bunch-of-keys.name")
    var bunchOfKeysName = ChatColor.BLUE.toString() + "Bunch of keys"

    @ConfigOptions(name = "bunch-of-keys.recipe")
    override var bunchOfKeysShape = listOf(" D ", "DBD", " D ")

    @ConfigOptions(name = "padlock-finder.name")
    var padlockFinderName = ChatColor.RED.toString() + "Padlock finder"

    @ConfigOptions(name = "recipe-materials-v1")
    override var shapeMaterials = LinkedHashMap<String, String>()

    override fun getKeyMaterialID(): String {
        return keyMaterial.name
    }

    override fun getPadlockFinderMaterialID(): String {
        return Material.COMPASS.name
    }

    /**
     * Creates a new plugin configuration instance.
     */
    init {
        shapeMaterials["A"] = Material.IRON_INGOT.name
        shapeMaterials["B"] = Material.LEVER.name
        shapeMaterials["C"] = Material.COMMAND_BLOCK.name
        shapeMaterials["D"] = Material.STRING.name
    }
}