package fr.skyost.serialkey.bukkit.command

import com.google.common.base.Joiner
import fr.skyost.serialkey.bukkit.BukkitTypeConverter
import fr.skyost.serialkey.bukkit.SerialKey
import fr.skyost.serialkey.core.command.GetKeyCommand
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

/**
 * Represents the <pre>/serialkey getkey</pre> command executor.
 */
class BukkitGetKeyCommand(plugin: SerialKey) : GetKeyCommand<ItemStack>(plugin), CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            return false
        }
        if (args[0].equals("getkey", ignoreCase = true)) {
            val player = if (sender is Player) sender else null
            val targeted = player?.getTargetBlock(null, 100)
            super.execute(
                    BukkitTypeConverter.toSerialKeyPerson(sender),
                    if (targeted == null) null else BukkitTypeConverter.toSerialKeyLocation(targeted.location),
                    if (player == null) Consumer { } else Consumer { item: ItemStack -> player.world.dropItemNaturally(player.eyeLocation, item) }
            )
            return true
        }
        return false
    }

    override fun copyItem(item: ItemStack): ItemStack {
        return item.clone()
    }

    /**
     * Returns the command's usage.
     *
     * @return The command's usage.
     */
    val usage: String
        get() {
            val description = (getPlugin() as SerialKey).description
            val builder = StringBuilder()
            builder.append(ChatColor.GOLD).append("------------------------------------\n")
            builder.append(ChatColor.GOLD).append(description.name)
            builder.append(ChatColor.RESET).append(" v").append(description.version).append("\n")
            builder.append("By ").append(ChatColor.GOLD).append(Joiner.on(' ').join(description.authors)).append("\n")
            builder.append(ChatColor.GOLD).append("------------------------------------\n")
            builder.append(ChatColor.RESET).append(ChatColor.BOLD).append("Commands :\n")
            builder.append(ChatColor.RESET).append(ChatColor.GOLD).append("/serialkey getkey")
            builder.append(ChatColor.RESET).append(" - Returns the key corresponding to your facing block.")
            return builder.toString()
        }
}