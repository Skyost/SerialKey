package fr.skyost.serialkey.sponge.config;

import fr.skyost.serialkey.core.config.SerialKeyMessages;

import java.nio.file.Path;

/**
 * The plugin messages class.
 */

public class SpongePluginMessages extends SpongeConfig implements SerialKeyMessages {

	@ConfigOptions(name = "messages.prefix")
	public String prefix = "&b[SerialKey]";
	@ConfigOptions(name = "messages.permission")
	public String messagePermission = "&cYou do not have the permission to perform this action.";
	@ConfigOptions(name = "messages.padlock-placed")
	public String messagePadlockPlaced = "&aPadlock placed ! If you want to remove it, you have to break this block.";
	@ConfigOptions(name = "messages.padlock-removed")
	public String messagePadlockRemoved = "&6Padlock removed.";
	@ConfigOptions(name = "messages.block-has-padlock")
	public String messageBlockHasPadlock = "&cThis block has a padlock.";
	@ConfigOptions(name = "messages.padlock-finder-enabled")
	public String messagePadlockFinderEnabled = "&aPadlock finder enabled ! Your compasses will now point to its object. You can reset it back to the spawn by doing another right click with any padlock finder.";
	@ConfigOptions(name = "messages.padlock-finder-disabled")
	public String messagePadlockFinderDisabled = "&cPadlock finder has been disabled.";
	@ConfigOptions(name = "messages.chest-protection")
	public String messageChestProtection = "&cYou can't place this key in this chest.";

	/**
	 * Creates a new plugin messages instance.
	 *
	 * @param file The config file.
	 */

	public SpongePluginMessages(final Path file) {
		super(file, "SerialKey Messages");
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