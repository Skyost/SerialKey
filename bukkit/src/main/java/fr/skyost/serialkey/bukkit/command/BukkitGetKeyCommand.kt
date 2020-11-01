package fr.skyost.serialkey.bukkit.command;

import com.google.common.base.Joiner;
import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.bukkit.SerialKey;
import fr.skyost.serialkey.core.command.GetKeyCommand;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Represents the <pre>/serialkey getkey</pre> command executor.
 */

public class BukkitGetKeyCommand extends GetKeyCommand<ItemStack> implements CommandExecutor {

	/**
	 * Creates a new <pre>/serialkey getkey</pre> command executor instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public BukkitGetKeyCommand(final SerialKey plugin) {
		super(plugin);
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if(args.length < 1) {
			return false;
		}

		if(args[0].equalsIgnoreCase("getkey")) {
			final Player player = sender instanceof Player ? (Player) sender : null;
			final Block targeted = player == null ? null : player.getTargetBlock(null, 100);

			super.execute(
					BukkitTypeConverter.toSerialKeyPerson(sender),
					targeted == null ? null : BukkitTypeConverter.toSerialKeyLocation(targeted.getLocation()),
					player == null ? item -> {} : (item -> player.getWorld().dropItemNaturally(player.getEyeLocation(), item))
			);
			return true;
		}

		return false;
	}

	@Override
	protected ItemStack copyItem(final ItemStack item) {
		return item.clone();
	}

	/**
	 * Returns the command's usage.
	 * 
	 * @return The command's usage.
	 */
	
	public final String getUsage() {
		final PluginDescriptionFile description = ((SerialKey)getPlugin()).getDescription();
		final StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD).append("------------------------------------\n");
		builder.append(ChatColor.GOLD).append(description.getName());
		builder.append(ChatColor.RESET).append(" v").append(description.getVersion()).append("\n");
		builder.append("By ").append(ChatColor.GOLD).append(Joiner.on(' ').join(description.getAuthors())).append("\n");
		builder.append(ChatColor.GOLD).append("------------------------------------\n");
		builder.append(ChatColor.RESET).append(ChatColor.BOLD).append("Commands :\n");
		builder.append(ChatColor.RESET).append(ChatColor.GOLD).append("/serialkey getkey");
		builder.append(ChatColor.RESET ).append(" - Returns the key corresponding to your facing block.");
		return builder.toString();
	}

}