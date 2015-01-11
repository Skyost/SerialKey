package fr.skyost.serialkey.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.google.common.base.Joiner;

import fr.skyost.serialkey.SerialKeyAPI;
import fr.skyost.serialkey.utils.Utils;

public class SerialKeyCommand implements CommandExecutor {

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if(!(sender instanceof Player)) {
			SerialKeyAPI.sendMessage(sender, ChatColor.RED + "You must be a player to run this command.");
			return true;
		}
		if(args.length < 1) {
			return false;
		}
		args[0] = args[0].toLowerCase();
		if(!sender.hasPermission("serialkey.command." + args[0])) {
			SerialKeyAPI.sendMessage(sender, SerialKeyAPI.getMessages().messagePermission);
			return true;
		}
		switch(args[0]) {
		case "getkey":
			final Block block = Utils.getTargetBlock((Player)sender, 20);
			if(block != null) {
				final Location location = block.getLocation();
				if(SerialKeyAPI.hasPadlock(location)) {
					final Player player = (Player)sender;
					player.getWorld().dropItemNaturally(player.getEyeLocation(), SerialKeyAPI.getKey(location));
					break;
				}
			}
			SerialKeyAPI.sendMessage(sender, ChatColor.RED + "You must be targeting to a valid block.");
			break;
		default:
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the command's usage.
	 * 
	 * @return The command's usage.
	 */
	
	public final String getUsage() {
		final PluginDescriptionFile desc = SerialKeyAPI.getPlugin().getDescription();
		final StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD + "------------------------------------\n");
		builder.append(desc.getName() + " v" + desc.getVersion() + "\n");
		builder.append("By " + Joiner.on(' ').join(desc.getAuthors()) + "\n");
		builder.append("------------------------------------\n");
		builder.append(ChatColor.RESET + "" + ChatColor.BOLD + "Commands :\n");
		builder.append(ChatColor.RESET + "/serialkey getkey - Gets the key corresponding to your facing block.");
		return builder.toString();
	}

}