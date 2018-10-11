package fr.skyost.serialkey.config;

import java.io.File;
import java.util.Collections;

import org.bukkit.ChatColor;

import fr.skyost.serialkey.util.Skyoconfig;

/**
 * The plugin messages class.
 */

public class PluginMessages extends Skyoconfig {
	
	@ConfigOptions(name = "messages.prefix")
	public String prefix = ChatColor.AQUA + "[SerialKey]";
	@ConfigOptions(name = "messages.permission")
	public String messagePermission = ChatColor.RED + "You do not have the permission to perform this action.";
	@ConfigOptions(name = "messages.1")
	public String message1 = ChatColor.GREEN + "Padlock placed ! If you want to remove it, you have to break this block.";
	@ConfigOptions(name = "messages.2")
	public String message2 = ChatColor.GOLD + "Padlock removed.";
	@ConfigOptions(name = "messages.3")
	public String message3 = ChatColor.RED + "This block has a padlock.";
	@ConfigOptions(name = "messages.4")
	public String message4 = ChatColor.GREEN + "Padlock finder enabled ! Your compasses will now point to its location. You can reset it back to the spawn by doing another right click with any padlock finder.";
	@ConfigOptions(name = "messages.5")
	public String message5 = ChatColor.RED + "Padlock finder has been disabled.";
	@ConfigOptions(name = "messages.6")
	public String message6 = ChatColor.RED + "A similar key is already present !";

	/**
	 * Creates a new plugin messages instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */

	public PluginMessages(final File dataFolder) {
		super(new File(dataFolder, "messages.yml"), Collections.singletonList("SerialKey messages"));
	}

}
