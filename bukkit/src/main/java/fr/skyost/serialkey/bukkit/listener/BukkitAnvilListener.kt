package fr.skyost.serialkey.bukkit.listener;

import fr.skyost.serialkey.core.SerialKeyPlugin;
import fr.skyost.serialkey.core.listener.AnvilListener;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * A listener that allows to listen anvil related events.
 */

public class BukkitAnvilListener extends AnvilListener<ItemStack, Location> implements Listener {

	/**
	 * Creates a new anvil listener instance.
	 *
	 * @param plugin The plugin.
	 */

	public BukkitAnvilListener(final SerialKeyPlugin<ItemStack, Location> plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onInventoryClick(final InventoryClickEvent event) {
		if(event.getInventory().getType() != InventoryType.ANVIL) {
			return;
		}
		super.onItemClicked(event.getCurrentItem(), () -> event.setCancelled(true));
	}

}