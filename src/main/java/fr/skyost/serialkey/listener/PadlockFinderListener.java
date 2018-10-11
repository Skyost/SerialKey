package fr.skyost.serialkey.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.skyost.serialkey.SerialKey;

/**
 * A listener that allows to listen padlock finder related events.
 */

public class PadlockFinderListener extends SerialKeyListener {

	/**
	 * Creates a new padlock finder listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public PadlockFinderListener(final SerialKey plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onPlayerInteract(final PlayerInteractEvent event) {
		if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final ItemStack item = event.getItem();
		if(!api.isUsedPadlockFinder(item)) {
			return;
		}

		final Player player = event.getPlayer();
		final Location spawn = player.getWorld().getSpawnLocation();
		if(player.getCompassTarget().equals(spawn)) {
			try {
				player.setCompassTarget(api.extractLocation(item));
				player.sendMessage(plugin.getPluginMessages().message4);
			}
			catch(final Exception ex) {
				ex.printStackTrace();
				plugin.sendMessage(player, ChatColor.RED + ex.getClass().getName());
			}
		}
		else {
			player.setCompassTarget(spawn);
			player.sendMessage(plugin.getPluginMessages().message5);
		}
		event.setCancelled(true);
	}
	
}
