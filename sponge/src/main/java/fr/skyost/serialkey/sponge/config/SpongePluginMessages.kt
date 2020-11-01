package fr.skyost.serialkey.sponge.config

import fr.skyost.serialkey.core.config.SerialKeyMessages
import java.nio.file.Path

/**
 * The plugin messages class.
 */
class SpongePluginMessages(file: Path) : SpongeConfig(file, listOf("SerialKey Messages")), SerialKeyMessages {
    @ConfigOptions(name = "messages.prefix")
    override var prefix = "&b[SerialKey]"

    @ConfigOptions(name = "messages.permission")
    override var permissionMessage = "&cYou do not have the permission to perform this action."

    @ConfigOptions(name = "messages.padlock-placed")
    override var padlockPlacedMessage = "&aPadlock placed ! If you want to remove it, you have to break this block."

    @ConfigOptions(name = "messages.padlock-removed")
    override var padlockRemovedMessage = "&6Padlock removed."

    @ConfigOptions(name = "messages.block-has-padlock")
    override var blockHasPadlockMessage = "&cThis block has a padlock."

    @ConfigOptions(name = "messages.padlock-finder-enabled")
    override var padlockFinderEnabledMessage = "&aPadlock finder enabled ! Your compasses will now point to its object. You can reset it back to the spawn by doing another right click with any padlock finder."

    @ConfigOptions(name = "messages.padlock-finder-disabled")
    override var padlockFinderDisabledMessage = "&cPadlock finder has been disabled."

    @ConfigOptions(name = "messages.chest-protection")
    override var chestProtectionMessage = "&cYou can't place this key in this chest."
}