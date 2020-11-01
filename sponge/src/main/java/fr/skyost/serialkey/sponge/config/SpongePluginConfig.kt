package fr.skyost.serialkey.sponge.config

import fr.skyost.serialkey.core.config.SerialKeyConfig
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import java.nio.file.Path
import java.util.*

/**
 * The plugin configuration class.
 */
class SpongePluginConfig(file: Path) : SpongeConfig(file, listOf("SerialKey Configuration")), SerialKeyConfig {
    @ConfigOptions(name = "enable.updater")
    var enableUpdater = true

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
    var keyMaterial: ItemType = ItemTypes.TRIPWIRE_HOOK

	@ConfigOptions(name = "key.name")
    var keyName = "&6Key"

    @ConfigOptions(name = "key.recipe")
    override var keyShape: List<String> = ArrayList(listOf("A", "B"))

	@ConfigOptions(name = "master-key.material")
    var masterKeyMaterial: ItemType = ItemTypes.NAME_TAG

	@ConfigOptions(name = "master-key.name")
    var masterKeyName = "&5Master Key"

    @ConfigOptions(name = "master-key.recipe")
    override var masterKeyShape: List<String> = ArrayList(listOf("C", "B"))

	@ConfigOptions(name = "bunch-of-keys.material")
    var bunchOfKeysMaterial: ItemType = ItemTypes.NAME_TAG

	@ConfigOptions(name = "bunch-of-keys.name")
    var bunchOfKeysName = "&9Bunch of keys"

    @ConfigOptions(name = "bunch-of-keys.recipe")
    override var bunchOfKeysShape: List<String> = ArrayList(listOf(" D ", "DBD", " D "))

	@ConfigOptions(name = "padlock-finder.name")
    var padlockFinderName = "&cPadlock finder"

	@ConfigOptions(name = "recipe-materials-v1")
    override var shapeMaterials = LinkedHashMap<String, String>()

    override fun getKeyMaterialID(): String {
        return keyMaterial.id
    }

    override fun getPadlockFinderMaterialID(): String {
        return ItemTypes.COMPASS.id
    }

    init {
        shapeMaterials["A"] = ItemTypes.IRON_INGOT.id
        shapeMaterials["B"] = ItemTypes.LEVER.id
        shapeMaterials["C"] = ItemTypes.COMMAND_BLOCK.id
        shapeMaterials["D"] = ItemTypes.STRING.id
    }
}