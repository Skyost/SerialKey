package fr.skyost.serialkey.sponge;

import com.google.inject.Inject;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.sponge.command.SpongeGetKeyCommand;
import fr.skyost.serialkey.sponge.config.SpongeConfig;
import fr.skyost.serialkey.sponge.config.SpongePluginConfig;
import fr.skyost.serialkey.sponge.config.SpongePluginData;
import fr.skyost.serialkey.sponge.config.SpongePluginMessages;
import fr.skyost.serialkey.sponge.item.SpongeItemManager;
import fr.skyost.serialkey.sponge.listener.*;
import fr.skyost.serialkey.sponge.padlock.SpongePadlockManager;
import fr.skyost.serialkey.sponge.unlocker.SpongeUnlocker;
import fr.skyost.serialkey.sponge.util.OreUpdater;
import fr.skyost.serialkey.sponge.util.Permission;
import fr.skyost.serialkey.sponge.util.Util;
import org.bstats.sponge.MetricsLite;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.nio.file.Path;

/**
 * The SerialKey plugin class.
 */

@Plugin(id = BuildConfig.PLUGIN_ID, name = BuildConfig.PLUGIN_NAME, description = BuildConfig.PLUGIN_DESCRIPTION, version = BuildConfig.VERSION, url = BuildConfig.PLUGIN_URL, authors = BuildConfig.PLUGIN_AUTHORS)
public class SerialKey implements SerialKeyPlugin<ItemStack, Location<World>> {

	/**
	 * The data folder.
	 */

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path dataFolder;

	/**
	 * bStats metrics.
	 */

	@Inject
	private MetricsLite metrics;

	/**
	 * The plugin's logger.
	 */

	@Inject
	private Logger logger;

	/**
	 * The plugin config.
	 */

	private SpongePluginConfig config;

	/**
	 * The plugin messages.
	 */

	private SpongePluginMessages messages;

	/**
	 * The item manager.
	 */

	private SpongeItemManager itemManager;

	/**
	 * The unlocker.
	 */

	private SpongeUnlocker unlocker;

	/**
	 * The padlock manager.
	 */

	private SpongePadlockManager padlockManager;

	@Listener
	public void onGamePreInitialize(final GamePreInitializationEvent event) throws SpongeConfig.InvalidConfigurationException {
		// TODO: Needs proper error handling
		// Configuration :

		config = new SpongePluginConfig(dataFolder.resolve("config.conf"));
		config.load();
		messages = new SpongePluginMessages(dataFolder.resolve("messages.conf"));
		messages.load();

		// Core object :

		itemManager = new SpongeItemManager(this);
		itemManager.createRecipes();
		unlocker = new SpongeUnlocker(this);
		padlockManager = new SpongePadlockManager(this);

		// Events :

		final EventManager eventManager = Sponge.getEventManager();
		eventManager.registerListeners(this, new SpongeGlobalListener(this));
		eventManager.registerListeners(this, new SpongeBlocksListener(this));
		eventManager.registerListeners(this, new SpongeBunchOfKeysListener(this));
		eventManager.registerListeners(this, new SpongePadlockFinderListener(this));
		if(config.disableHoppers) {
			eventManager.registerListeners(this, new SpongeHopperListener(this));
		}
		if(!config.allowLostChests) {
			eventManager.registerListeners(this, new SpongeLostChestsListener(this));
		}

		// Commands :

		Sponge.getServiceManager().getRegistration(PermissionService.class).ifPresent(registration -> registerPermissions(registration.getProvider()));
		Sponge.getCommandManager().register(this, CommandSpec.builder()
				.description(Text.of("Main command of SerialKey."))
				.child(
						CommandSpec.builder()
								.description(Text.of("Returns the key of the targeted block."))
								.permission("serialkey.command.getkey")
								.executor(new SpongeGetKeyCommand(this))
								.build(), "getkey"
				)
				.build(), "serial-key", "sk");
		
		// Services :

		if(config.enableUpdater) {
			new OreUpdater(logger).start();
		}
	}

	@Listener
	public void onGameStarted(final GameStartedServerEvent event) throws SpongeConfig.InvalidConfigurationException {
		final SpongePluginData data = new SpongePluginData(dataFolder.resolve("data.conf"));
		data.load();
		padlockManager.load(data);
	}

	@Listener
	public void onGameStop(final GameStoppingEvent event) throws SpongeConfig.InvalidConfigurationException {
		final SpongePluginData data = new SpongePluginData(dataFolder.resolve("data.conf"));
		padlockManager.save(data);
		data.save();
	}

	@Override
	public SpongeItemManager getItemManager() {
		return itemManager;
	}

	@Override
	public SpongeUnlocker getUnlocker() {
		return unlocker;
	}

	@Override
	public SpongePadlockManager getPadlockManager() {
		return padlockManager;
	}

	@Override
	public SpongePluginConfig getPluginConfig() {
		return config;
	}

	@Override
	public SpongePluginMessages getPluginMessages() {
		return messages;
	}

	/**
	 * Sends a message with the plugin's prefix.
	 *
	 * @param receiver Who receives the message.
	 * @param message The message.
	 */

	public void sendMessage(final CommandSource receiver, final String message) {
		receiver.sendMessage(Util.parseString(messages.prefix + " " + message));
	}

	/**
	 * Registers all plugin permissions.
	 *
	 * @param service The permission service.
	 */

	private void registerPermissions(final PermissionService service) {
		final Permission[] permissions = new Permission[]{
				new Permission("serialkey.craft.key", PermissionDescription.ROLE_USER, "Allows you to craft a key."),
				new Permission("serialkey.craft.masterkey", PermissionDescription.ROLE_STAFF, "Allows you to craft a master key."),
				new Permission("serialkey.craft.keyclone", PermissionDescription.ROLE_USER, "Allows you to craft a key clone."),
				new Permission("serialkey.craft.bunchofkeys", PermissionDescription.ROLE_USER, "Allows you to craft a bunch of keys."),
				new Permission("serialkey.craft.padlockfinder", PermissionDescription.ROLE_USER, "Allows you to craft a padlock finder."),

				new Permission("serialkey.use.key", PermissionDescription.ROLE_USER, "Allows you to use a key."),
				new Permission("serialkey.use.masterkey", PermissionDescription.ROLE_STAFF, "Allows you to use a master key."),
				new Permission("serialkey.use.bunchofkeys", PermissionDescription.ROLE_USER, "Allows you to use a bunch of keys."),
				new Permission("serialkey.use.padlockfinder", PermissionDescription.ROLE_USER, "Allows you to use a padlock finder."),

				new Permission("serialkey.command.getkey", PermissionDescription.ROLE_STAFF, "Allows you to use /serialkey getkey.")
		};
		for(final Permission permission : permissions) {
			final PermissionDescription.Builder builder = service.newDescriptionBuilder(this);
			builder.id(permission.getPermission());
			builder.assign(permission.getRole(), true);
			builder.description(permission.getDescription());
			builder.register();
		}
	}

}