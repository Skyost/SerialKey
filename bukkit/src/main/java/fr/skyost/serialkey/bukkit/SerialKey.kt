package fr.skyost.serialkey.bukkit;

import fr.skyost.serialkey.bukkit.command.BukkitGetKeyCommand;
import fr.skyost.serialkey.bukkit.config.BukkitPluginConfig;
import fr.skyost.serialkey.bukkit.config.BukkitPluginData;
import fr.skyost.serialkey.bukkit.config.BukkitPluginMessages;
import fr.skyost.serialkey.bukkit.item.BukkitItemManager;
import fr.skyost.serialkey.bukkit.listener.*;
import fr.skyost.serialkey.bukkit.padlock.BukkitPadlockManager;
import fr.skyost.serialkey.bukkit.unlocker.BukkitUnlocker;
import fr.skyost.serialkey.bukkit.util.Skyupdater;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * The SerialKey plugin class.
 */

public class SerialKey extends JavaPlugin implements SerialKeyPlugin<ItemStack, Location> {

	/**
	 * The plugin config.
	 */
	
	private BukkitPluginConfig config;

	/**
	 * The plugin messages.
	 */

	private BukkitPluginMessages messages;

	/**
	 * The item manager.
	 */

	private BukkitItemManager itemManager;

	/**
	 * The unlocker.
	 */

	private BukkitUnlocker unlocker;

	/**
	 * The padlock manager.
	 */

	private BukkitPadlockManager padlockManager;
	
	@Override
	public final void onEnable() {
		try {
			final File dataFolder = this.getDataFolder();
			
			// Configuration :
			
			config = new BukkitPluginConfig(dataFolder);
			config.load();
			messages = new BukkitPluginMessages(dataFolder);
			messages.load();
			final BukkitPluginData data = new BukkitPluginData(dataFolder);
			data.load();

			// Core object :

			itemManager = new BukkitItemManager(this);
			itemManager.createRecipes();
			unlocker = new BukkitUnlocker(this);
			padlockManager = new BukkitPadlockManager(this);
			padlockManager.load(data);

			// Events :
			
			final PluginManager manager = Bukkit.getPluginManager();
			manager.registerEvents(new BukkitGlobalListener(this), this);
			manager.registerEvents(new BukkitBlocksListener(this), this);
			manager.registerEvents(new BukkitBunchOfKeysListener(this), this);
			manager.registerEvents(new BukkitPadlockFinderListener(this), this);
			if(!config.canRenameItems) {
				manager.registerEvents(new BukkitAnvilListener(this), this);
			}
			if(config.disableHoppers) {
				manager.registerEvents(new BukkitHopperListener(this), this);
			}
			if(!config.allowLostChests) {
				manager.registerEvents(new BukkitLostChestsListener(this), this);
			}
			
			// Commands :
			
			final BukkitGetKeyCommand executor = new BukkitGetKeyCommand(this);
			final PluginCommand command = this.getCommand("serialkey");
			command.setUsage(executor.getUsage());
			command.setExecutor(executor);
			
			// Services :
			
			if(config.enableUpdater) {
				new Skyupdater(this, 84423, this.getFile(), true, true);
			}
			if(config.enableMetrics) {
				new MetricsLite(this);
			}
		}
		catch(final NullPointerException ex) {
			sendMessage(Bukkit.getConsoleSender(), ChatColor.RED + "Null pointer exception ! Maybe you have misconfigured one (or more) item recipe.");
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public final void onDisable() {
		try {
			final BukkitPluginData data = new BukkitPluginData(getDataFolder());
			padlockManager.save(data);
			data.save();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public BukkitItemManager getItemManager() {
		return itemManager;
	}

	@Override
	public BukkitUnlocker getUnlocker() {
		return unlocker;
	}

	@Override
	public BukkitPadlockManager getPadlockManager() {
		return padlockManager;
	}

	@Override
	public BukkitPluginConfig getPluginConfig() {
		return config;
	}

	@Override
	public BukkitPluginMessages getPluginMessages() {
		return messages;
	}

	/**
	 * Sends a message with the plugin prefix.
	 *
	 * @param sender Who receives the message.
	 * @param message The message.
	 */

	public void sendMessage(final CommandSender sender, final String message) {
		sender.sendMessage(messages.prefix + " " + message);
	}
	
}