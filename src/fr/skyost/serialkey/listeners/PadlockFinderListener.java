package fr.skyost.serialkey.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.skyost.serialkey.SerialKeyAPI;

public class PadlockFinderListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		switch(event.getAction()) {
		case RIGHT_CLICK_BLOCK:
		case RIGHT_CLICK_AIR:
			final Player player = event.getPlayer();
			final ItemStack item = event.getItem();
			if(SerialKeyAPI.isUsedPadlockFinder(item)) {
				final Location spawn = player.getWorld().getSpawnLocation();
				if(player.getCompassTarget().equals(spawn)) {
					player.setCompassTarget(SerialKeyAPI.extractLocation(item));
					player.sendMessage(SerialKeyAPI.getMessages().message4);
				}
				else {
					player.setCompassTarget(spawn);
					player.sendMessage(SerialKeyAPI.getMessages().message5);
				}
				event.setCancelled(true);
			}
		default:
			break;
		}
	}
	
}
