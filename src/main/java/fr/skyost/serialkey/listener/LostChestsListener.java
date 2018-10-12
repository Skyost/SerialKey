package fr.skyost.serialkey.listener;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import fr.skyost.serialkey.SerialKey;

/**
 * A listener that allows to listen chests related events.
 */

public class LostChestsListener extends SerialKeyListener {

	/**
	 * Creates a new lost chests listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public LostChestsListener(final SerialKey plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onInventoryClick(final InventoryClickEvent event) {
		if(!api.isUsedKey(event.getCurrentItem())) {
			return;
		}

		final InventoryHolder holder = event.getInventory().getHolder();
		if(!(holder instanceof Chest)) {
			return;
		}

		final Location location = ((Chest)holder).getLocation();
		if(api.hasPadlock(location) && api.extractLocation(event.getCurrentItem()).equals(location)) {
			event.setCancelled(true);
			plugin.sendMessage(event.getWhoClicked(), plugin.getPluginMessages().message6);
		}
	}

}