package fr.skyost.serialkey;

import java.io.File;
import java.util.Arrays;

import org.bukkit.ChatColor;

import fr.skyost.serialkey.utils.Skyoconfig;

public class PluginMessages extends Skyoconfig {
	
	@ConfigOptions(name = "messages.prefix")
	public String prefix = ChatColor.AQUA + "[SerialKey]";
	@ConfigOptions(name = "messages.1")
	public String message1 = ChatColor.GREEN + "Padlock placed ! If you want to remove it, you have to break this block.";
	@ConfigOptions(name = "messages.2")
	public String message2 = ChatColor.RED + "This door already has a padlock !";
	@ConfigOptions(name = "messages.3")
	public String message3 = ChatColor.RED + "This chest already has a padlock !";
	@ConfigOptions(name = "messages.4")
	public String message4 = ChatColor.GOLD + "Padlock removed.";
	@ConfigOptions(name = "messages.5")
	public String message5 = ChatColor.RED + "This block has a padlock.";
	@ConfigOptions(name = "messages.6")
	public String message6 = ChatColor.RED + "You do not have the permission to perform this action.";
	@ConfigOptions(name = "messages.7")
	public String message7 = ChatColor.RED + "You must place this chest on the right of the other chest.";

	protected PluginMessages(final File dataFolder) {
		super(new File(dataFolder, "messages.yml"), Arrays.asList("SerialKey messages"));
	}

}
