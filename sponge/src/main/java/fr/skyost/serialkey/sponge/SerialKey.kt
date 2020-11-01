package fr.skyost.serialkey.sponge

import com.google.inject.Inject
import fr.skyost.serialkey.core.SerialKeyPlugin
import fr.skyost.serialkey.sponge.command.SpongeGetKeyCommand
import fr.skyost.serialkey.sponge.config.SpongeConfig
import fr.skyost.serialkey.sponge.config.SpongePluginConfig
import fr.skyost.serialkey.sponge.config.SpongePluginData
import fr.skyost.serialkey.sponge.config.SpongePluginMessages
import fr.skyost.serialkey.sponge.item.SpongeItemManager
import fr.skyost.serialkey.sponge.listener.*
import fr.skyost.serialkey.sponge.padlock.SpongePadlockManager
import fr.skyost.serialkey.sponge.unlocker.SpongeUnlocker
import fr.skyost.serialkey.sponge.util.OreUpdater
import fr.skyost.serialkey.sponge.util.Permission
import fr.skyost.serialkey.sponge.util.Util
import org.bstats.sponge.MetricsLite2
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppingEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.service.ProviderRegistration
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.nio.file.Path

/**
 * The SerialKey plugin class.
 */
@Plugin(id = BuildConfig.PLUGIN_ID, name = BuildConfig.PLUGIN_NAME, description = BuildConfig.PLUGIN_DESCRIPTION, version = BuildConfig.VERSION, url = BuildConfig.PLUGIN_URL, authors = [BuildConfig.PLUGIN_AUTHORS], dependencies = [Dependency(id = "spongeapi", version = BuildConfig.SPONGE_VERSION)])
class SerialKey @Inject constructor(metricsFactory: MetricsLite2.Factory) : SerialKeyPlugin<ItemStack, Location<World>> {
    /**
     * The data folder.
     */
    @Inject
    @ConfigDir(sharedRoot = false)
    private lateinit var dataFolder: Path

    /**
     * bStats metrics.
     */
    private val metrics: MetricsLite2 = metricsFactory.make(60)

    /**
     * The plugin's logger.
     */
    @Inject
    private lateinit var logger: Logger

    /**
     * The plugin config.
     */
    override lateinit var pluginConfig: SpongePluginConfig

    /**
     * The plugin messages.
     */
    override lateinit var pluginMessages: SpongePluginMessages

    /**
     * The item manager.
     */
    override val itemManager: SpongeItemManager = SpongeItemManager(this)

    /**
     * The unlocker.
     */
    override val unlocker: SpongeUnlocker = SpongeUnlocker(this)

    /**
     * The padlock manager.
     */
    override val padlockManager: SpongePadlockManager = SpongePadlockManager(this)

    @Listener
    @Throws(SpongeConfig.InvalidConfigurationException::class)
    fun onGamePreInitialize(event: GamePreInitializationEvent?) {
        // Configuration :
        pluginConfig = SpongePluginConfig(dataFolder.resolve("config.conf"))
        pluginConfig.load()
        pluginMessages = SpongePluginMessages(dataFolder.resolve("messages.conf"))
        pluginMessages.load()

        // Events :
        val eventManager = Sponge.getEventManager()
        eventManager.registerListeners(this, SpongeGlobalListener(this))
        eventManager.registerListeners(this, SpongeBlocksListener(this))
        eventManager.registerListeners(this, SpongeBunchOfKeysListener(this))
        eventManager.registerListeners(this, SpongePadlockFinderListener(this))
        if (!pluginConfig.canRenameItems) {
            eventManager.registerListeners(this, SpongeAnvilListener(this))
        }
        if (pluginConfig.disableHoppers) {
            eventManager.registerListeners(this, SpongeHopperListener(this))
        }
        if (!pluginConfig.allowLostChests) {
            eventManager.registerListeners(this, SpongeLostChestsListener(this))
        }

        // Commands :
        Sponge.getServiceManager().getRegistration(PermissionService::class.java).ifPresent { registration: ProviderRegistration<PermissionService> -> registerPermissions(registration.provider) }
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("Main command of SerialKey."))
                .child(
                        CommandSpec.builder()
                                .description(Text.of("Returns the key of the targeted block."))
                                .permission("serialkey.command.getkey")
                                .executor(SpongeGetKeyCommand(this))
                                .build(), "getkey"
                )
                .build(), "serial-key", "sk")

        // Services :
        if (pluginConfig.enableUpdater) {
            OreUpdater(logger).start()
        }
    }

    @Listener
    @Throws(SpongeConfig.InvalidConfigurationException::class)
    fun onGameStarted(event: GameStartedServerEvent?) {
        val data = SpongePluginData(dataFolder.resolve("data.conf"))
        data.load()
        padlockManager.load(data)
    }

    @Listener
    @Throws(SpongeConfig.InvalidConfigurationException::class)
    fun onGameStop(event: GameStoppingEvent?) {
        val data = SpongePluginData(dataFolder.resolve("data.conf"))
        padlockManager.save(data)
        data.save()
    }

    /**
     * Sends a message with the plugin's prefix.
     *
     * @param receiver Who receives the message.
     * @param message The message.
     */
    fun sendMessage(receiver: CommandSource, message: String) {
        receiver.sendMessage(Util.parseString(pluginMessages.prefix + " " + message))
    }

    /**
     * Registers all plugin permissions.
     *
     * @param service The permission service.
     */
    private fun registerPermissions(service: PermissionService) {
        val permissions = arrayOf(
                Permission("serialkey.craft.key", PermissionDescription.ROLE_USER, "Allows you to craft a key."),
                Permission("serialkey.craft.masterkey", PermissionDescription.ROLE_STAFF, "Allows you to craft a master key."),
                Permission("serialkey.craft.keyclone", PermissionDescription.ROLE_USER, "Allows you to craft a key clone."),
                Permission("serialkey.craft.bunchofkeys", PermissionDescription.ROLE_USER, "Allows you to craft a bunch of keys."),
                Permission("serialkey.craft.padlockfinder", PermissionDescription.ROLE_USER, "Allows you to craft a padlock finder."),
                Permission("serialkey.use.key", PermissionDescription.ROLE_USER, "Allows you to use a key."),
                Permission("serialkey.use.masterkey", PermissionDescription.ROLE_STAFF, "Allows you to use a master key."),
                Permission("serialkey.use.bunchofkeys", PermissionDescription.ROLE_USER, "Allows you to use a bunch of keys."),
                Permission("serialkey.use.padlockfinder", PermissionDescription.ROLE_USER, "Allows you to use a padlock finder."),
                Permission("serialkey.command.getkey", PermissionDescription.ROLE_STAFF, "Allows you to use /serialkey getkey.")
        )
        for (permission in permissions) {
            val builder = service.newDescriptionBuilder(this)
            builder.id(permission.permission)
            builder.assign(permission.role, true)
            builder.description(permission.description)
            builder.register()
        }
    }
}