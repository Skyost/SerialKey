package fr.skyost.serialkey.command;

import com.google.common.base.Joiner;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import fr.skyost.serialkey.SerialKey;

/**
 * Represents the <pre>/serialkey</pre> command executor.
 */

public class SerialKeyCommand implements CommandExecutor {

	/**
	 * The plugin instance.
	 */

	private final SerialKey plugin;

	/**
	 * Creates a new <pre>/serialkey</pre> command executor instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public SerialKeyCommand(final SerialKey plugin) {
		this.plugin = plugin;
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if(!(sender instanceof Player)) {
			plugin.sendMessage(sender, ChatColor.RED + "You must be a player to run this command.");
			return true;
		}

		if(args.length < 1) {
			return false;
		}
		args[0] = args[0].toLowerCase();

		if(!sender.hasPermission("serialkey.command." + args[0])) {
			plugin.sendMessage(sender, plugin.getPluginMessages().messagePermission);
			return true;
		}

		switch(args[0]) {
		case "getkey":
			final Block block = ((Player)sender).getTargetBlock(null, 100);
			if(block != null) {
				final Location location = block.getLocation();
				if(plugin.getAPI().hasPadlock(location)) {
					try {
						final Player player = (Player)sender;
						player.getWorld().dropItemNaturally(player.getEyeLocation(), plugin.getAPI().getKey(location));
					}
					catch(final Exception ex) {
						ex.printStackTrace();
						plugin.sendMessage(sender, ChatColor.RED + ex.getClass().getName());
					}
					break;
				}
			}

			plugin.sendMessage(sender, ChatColor.RED + "You must be targeting to a valid block.");
			break;
		default:
			return false;
		}

		return true;
	}

	/**
	 * Returns the plugin instance.
	 *
	 * @return The plugin instance.
	 */

	public SerialKey getPlugin() {
		return plugin;
	}

	/**
	 * Returns the command's usage.
	 * 
	 * @return The command's usage.
	 */
	
	public final String getUsage() {
		final PluginDescriptionFile description = plugin.getDescription();
		final StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD).append("------------------------------------\n");
		builder.append(description.getName()).append(" v").append(description.getVersion()).append("\n");
		builder.append("By ").append(Joiner.on(' ').join(description.getAuthors())).append("\n");
		builder.append("------------------------------------\n");
		builder.append(ChatColor.RESET).append(ChatColor.BOLD).append("Commands :\n");
		builder.append(ChatColor.RESET).append("/serialkey getkey - Returns the key corresponding to your facing block.");
		return builder.toString();
	}

}