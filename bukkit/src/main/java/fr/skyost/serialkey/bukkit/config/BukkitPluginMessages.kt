package fr.skyost.serialkey.bukkit.config

import fr.skyost.serialkey.bukkit.util.Skyoconfig
import fr.skyost.serialkey.core.config.SerialKeyMessages
import org.bukkit.ChatColor
import java.io.File

/**
 * The plugin messages class.
 */
class BukkitPluginMessages(dataFolder: File) : Skyoconfig(File(dataFolder, "messages.yml"), listOf("SerialKey Messages")), SerialKeyMessages {
    @ConfigOptions(name = "messages.prefix")
    override var prefix = ChatColor.AQUA.toString() + "[SerialKey]"

    @ConfigOptions(name = "messages.permission")
    override var permissionMessage = ChatColor.RED.toString() + "You do not have the permission to perform this action."

    @ConfigOptions(name = "messages.padlock-placed")
    override var padlockPlacedMessage = ChatColor.GREEN.toString() + "Padlock placed ! If you want to remove it, you have to break this block."

    @ConfigOptions(name = "messages.padlock-removed")
    override var padlockRemovedMessage = ChatColor.GOLD.toString() + "Padlock removed."

    @ConfigOptions(name = "messages.block-has-padlock")
    override var blockHasPadlockMessage = ChatColor.RED.toString() + "This block has a padlock."

    @ConfigOptions(name = "messages.padlock-finder-enabled")
    override var padlockFinderEnabledMessage = ChatColor.GREEN.toString() + "Padlock finder enabled ! Your compasses will now point to its object. You can reset it back to the spawn by doing another right click with any padlock finder."

    @ConfigOptions(name = "messages.padlock-finder-disabled")
    override var padlockFinderDisabledMessage = ChatColor.RED.toString() + "Padlock finder has been disabled."

    @ConfigOptions(name = "messages.chest-protection")
    override var chestProtectionMessage = ChatColor.RED.toString() + "You can't place this key in this chest."
}