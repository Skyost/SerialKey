package fr.skyost.serialkey.bukkit

import fr.skyost.serialkey.bukkit.command.BukkitGetKeyCommand
import fr.skyost.serialkey.bukkit.config.BukkitPluginConfig
import fr.skyost.serialkey.bukkit.config.BukkitPluginData
import fr.skyost.serialkey.bukkit.config.BukkitPluginMessages
import fr.skyost.serialkey.bukkit.item.BukkitItemManager
import fr.skyost.serialkey.bukkit.listener.*
import fr.skyost.serialkey.bukkit.padlock.BukkitPadlockManager
import fr.skyost.serialkey.bukkit.unlocker.BukkitUnlocker
import fr.skyost.serialkey.bukkit.util.Skyupdater
import fr.skyost.serialkey.core.SerialKeyPlugin
import org.bstats.bukkit.MetricsLite
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.java.JavaPlugin

/**
 * The SerialKey plugin class.
 */
class SerialKey : JavaPlugin(), SerialKeyPlugin<ItemStack, Location> {
    /**
     * The plugin config.
     */
    override lateinit var pluginConfig: BukkitPluginConfig

    /**
     * The plugin messages.
     */
    override lateinit var pluginMessages: BukkitPluginMessages

    /**
     * The item manager.
     */
    override val itemManager: BukkitItemManager = BukkitItemManager(this)

    /**
     * The unlocker.
     */
    override val unlocker: BukkitUnlocker = BukkitUnlocker(this)

    /**
     * The padlock manager.
     */
    override val padlockManager: BukkitPadlockManager = BukkitPadlockManager(this)

    init {
        pluginConfig = BukkitPluginConfig(dataFolder)
        pluginMessages = BukkitPluginMessages(dataFolder)
    }

    override fun onEnable() {
        try {
            val dataFolder = dataFolder

            // Configuration :
            pluginConfig = BukkitPluginConfig(dataFolder)
            pluginConfig.load()
            pluginMessages = BukkitPluginMessages(dataFolder)
            pluginMessages.load()
            val data = BukkitPluginData(dataFolder)
            data.load()

            // Core object :
            itemManager.createRecipes<Recipe>(Bukkit::addRecipe)
            padlockManager.load(data)

            // Events :
            val manager = Bukkit.getPluginManager()
            manager.registerEvents(BukkitGlobalListener(this), this)
            manager.registerEvents(BukkitBlocksListener(this), this)
            manager.registerEvents(BukkitBunchOfKeysListener(this), this)
            manager.registerEvents(BukkitPadlockFinderListener(this), this)
            if (!pluginConfig.canRenameItems) {
                manager.registerEvents(BukkitAnvilListener(this), this)
            }
            if (pluginConfig.disableHoppers) {
                manager.registerEvents(BukkitHopperListener(this), this)
            }
            if (!pluginConfig.allowLostChests) {
                manager.registerEvents(BukkitLostChestsListener(this), this)
            }

            // Commands :
            val executor = BukkitGetKeyCommand(this)
            val command = getCommand("serialkey")
            command!!.usage = executor.usage
            command.setExecutor(executor)

            // Services :
            if (pluginConfig.enableUpdater) {
                Skyupdater(this, 84423, file, true, true)
            }
            if (pluginConfig.enableMetrics) {
                MetricsLite(this, 60)
            }
        } catch (ex: NullPointerException) {
            sendMessage(Bukkit.getConsoleSender(), ChatColor.RED.toString() + "Null pointer exception ! Maybe you have misconfigured one (or more) item recipe.")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onDisable() {
        try {
            val data = BukkitPluginData(dataFolder)
            padlockManager.save(data)
            data.save()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * Sends a message with the plugin prefix.
     *
     * @param sender Who receives the message.
     * @param message The message.
     */
    private fun sendMessage(sender: CommandSender, message: String) {
        sender.sendMessage(pluginMessages.prefix + " " + message)
    }
}