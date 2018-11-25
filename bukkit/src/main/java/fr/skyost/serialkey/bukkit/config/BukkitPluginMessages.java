package fr.skyost.serialkey.bukkit.config;

import fr.skyost.serialkey.bukkit.util.Skyoconfig;
import fr.skyost.serialkey.core.config.SerialKeyMessages;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Collections;

/**
 * The plugin messages class.
 */

public class BukkitPluginMessages extends Skyoconfig implements SerialKeyMessages {
	
	@ConfigOptions(name = "messages.prefix")
	public String prefix = ChatColor.AQUA + "[SerialKey]";
	@ConfigOptions(name = "messages.permission")
	public String messagePermission = ChatColor.RED + "You do not have the permission to perform this action.";
	@ConfigOptions(name = "messages.padlock-placed")
	public String messagePadlockPlaced = ChatColor.GREEN + "Padlock placed ! If you want to remove it, you have to break this block.";
	@ConfigOptions(name = "messages.padlock-removed")
	public String messagePadlockRemoved = ChatColor.GOLD + "Padlock removed.";
	@ConfigOptions(name = "messages.block-has-padlock")
	public String messageBlockHasPadlock = ChatColor.RED + "This block has a padlock.";
	@ConfigOptions(name = "messages.padlock-finder-enabled")
	public String messagePadlockFinderEnabled = ChatColor.GREEN + "Padlock finder enabled ! Your compasses will now point to its object. You can reset it back to the spawn by doing another right click with any padlock finder.";
	@ConfigOptions(name = "messages.padlock-finder-disabled")
	public String messagePadlockFinderDisabled = ChatColor.RED + "Padlock finder has been disabled.";
	@ConfigOptions(name = "messages.chest-protection")
	public String messageChestProtection = ChatColor.RED + "You can't place this key in this chest.";

	/**
	 * Creates a new plugin messages instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */

	public BukkitPluginMessages(final File dataFolder) {
		super(new File(dataFolder, "messages.yml"), Collections.singletonList("SerialKey Messages"));
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getPermissionMessage() {
		return messagePermission;
	}

	@Override
	public String getPadlockPlacedMessage() {
		return messagePadlockPlaced;
	}

	@Override
	public String getPadlockRemovedMessage() {
		return messagePadlockRemoved;
	}

	@Override
	public String getBlockHasPadlockMessage() {
		return messageBlockHasPadlock;
	}

	@Override
	public String getPadlockFinderEnabledMessage() {
		return messagePadlockFinderEnabled;
	}

	@Override
	public String getPadlockFinderDisabledMessage() {
		return messagePadlockFinderDisabled;
	}

	@Override
	public String getChestProtectionMessage() {
		return messageChestProtection;
	}

}
