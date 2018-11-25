package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.bukkit.BukkitTypeConverter;
import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.LostChestsListener;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * A listener that allows to listen chests related events.
 */

public class BukkitLostChestsListener extends LostChestsListener<ItemStack, Location> implements Listener {

	/**
	 * Creates a new lost chests listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitLostChestsListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onInventoryClick(final InventoryClickEvent event) {
		final InventoryHolder holder = event.getInventory().getHolder();

		Location location = null;
		if(holder instanceof Chest) {
			location = ((Chest)holder).getLocation();
		}
		else if(holder instanceof DoubleChest) {
			location = ((DoubleChest)holder).getLocation();
		}

		if(location == null) {
			return;
		}

		super.onInventoryClick(
				BukkitTypeConverter.toSerialKeyPerson(event.getWhoClicked()),
				BukkitTypeConverter.toSerialKeyLocation(location),
				event.getCurrentItem(),
				() -> event.setCancelled(true)
		);
	}

}